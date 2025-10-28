package dev.adlin.producer;

import dev.adlin.config.ChatConfig;
import dev.adlin.discord.audio.AudioBufferManager;
import dev.adlin.discord.audio.AudioProvider;
import dev.adlin.manager.ModelsManager;
import dev.adlin.service.ModelService;
import dev.adlin.speech.synthesis.SpeechSynthesis;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ChatProducer {

    private static final Logger log = LogManager.getLogger(ChatProducer.class);
    private final ConcurrentHashMap<String, String> translatedMessages = new ConcurrentHashMap<>();

    private final AudioProvider audioProvider;
    private final ModelService modelService;
    private final SpeechSynthesis speechSynthesis;
    private final ChatConfig chatConfig;

    public ChatProducer(AudioBufferManager audioBufferManager,
                        AudioProvider audioProvider,
                        ModelService modelService,
                        ModelsManager modelsManager,
                        ChatConfig chatConfig

    ) {
        this.audioProvider = audioProvider;
        this.modelService = modelService;
        this.speechSynthesis = modelsManager.getSpeechSynthesisModel();
        this.chatConfig = chatConfig;

        audioBufferManager.setBufferListener((user, data) ->
                CompletableFuture.runAsync(() -> {
                    String transcript = modelsManager.getSpeechRecognitionModel().transcriptAudio(data);
                    translatedMessages.put(user.getName(), transcript);
                })
        );
    }

    @Scheduled(fixedDelay = 2000)
    private void process() {
            if (translatedMessages.isEmpty() && modelService.getProcessing().get()) return;

            if (translatedMessages.containsKey(chatConfig.getOwnerName())) {
                Set<Map.Entry<String, String>> entrySet =  translatedMessages.entrySet();
                Set<Map.Entry<String, String>> entryFiltered =  entrySet.stream()
                        .filter(entry -> entry.getKey().equals(chatConfig.getOwnerName()))
                        .collect(Collectors.toSet());

                for (Map.Entry<String, String> entry : entryFiltered) {
                    this.processAnswer(new UserMessage(chatConfig.getOwnerName() + ": " + entry.getValue()));
                    entrySet.remove(entry);
                }
            } else if (translatedMessages.size() > 1) {
                StringBuilder builder = new StringBuilder();
                builder.append("Ответь на эти вопросы общими словами или проигнорируй\n");

                Set<Map.Entry<String, String>> entrySet = translatedMessages.entrySet();
                int i = 1;
                for (Map.Entry<String, String> entry : entrySet) {
                    String username = entry.getKey();
                    String transcript = entry.getValue();
                    builder.append(i)
                            .append(". ")
                            .append(username)
                            .append(": ")
                            .append(transcript)
                            .append("\n");
                    entrySet.remove(entry);
                    i++;
                }

                SystemMessage systemMessage = new SystemMessage(builder.toString());
                processAnswer(systemMessage);

            } else {
                translatedMessages.forEach((username, transcript) ->
                        processAnswer(new UserMessage(username + ": " + transcript)));
                translatedMessages.clear();
            }
    }

    private void processAnswer(Message message) {
        AssistantMessage assistantMessage = this.modelService.ask(message);
        
        byte[] speech = speechSynthesis.speech(assistantMessage.getText());
        audioProvider.addAudio(speech);
    }

}
