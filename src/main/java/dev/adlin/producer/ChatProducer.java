package dev.adlin.producer;

import dev.adlin.config.ChatConfig;
import dev.adlin.discord.audio.AudioBufferManager;
import dev.adlin.discord.audio.AudioProvider;
import dev.adlin.manager.ModelsManager;
import dev.adlin.service.ModelService;
import dev.adlin.speech.synthesis.SpeechSynthesis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class ChatProducer {

    private static final Logger log = LogManager.getLogger(ChatProducer.class);
    private final ConcurrentHashMap<String, String> translatedMessages = new ConcurrentHashMap<>();

    private final AudioProvider audioProvider;
    private final ModelService modelService;
    private final SpeechSynthesis speechSynthesis;
    private final ChatConfig chatConfig;
    private final ModelsManager modelsManager;

    public ChatProducer(AudioBufferManager audioBufferManager,
                        AudioProvider audioProvider,
                        ModelService modelService,
                        ModelsManager modelsManager,
                        ChatConfig chatConfig) {
        this.audioProvider = audioProvider;
        this.modelService = modelService;
        this.modelsManager = modelsManager;
        this.chatConfig = chatConfig;

        this.speechSynthesis = modelsManager.getSpeechSynthesisModel();

        audioBufferManager.setBufferListener((user, data) -> {
            if (modelsManager.getRecognitionModelState().isEnabled()) {
                CompletableFuture.runAsync(() -> {
                    String transcript = modelsManager
                            .getSpeechRecognitionModel()
                            .transcriptAudio(data);

                    if (transcript != null && !transcript.isBlank())
                        translatedMessages.put(user.getName(), transcript);
                });
            }
        });
    }

    @Scheduled(fixedDelay = 2000)
    private void process() {
        if (translatedMessages.isEmpty() && !modelService.getProcessing().get()) {
            return;
        }

        if (translatedMessages.containsKey(chatConfig.getOwnerName())) {
            String text = translatedMessages.remove(chatConfig.getOwnerName());
            processAnswer(new UserMessage(chatConfig.getOwnerName() + ": " + text));
            return;
        }

        if (translatedMessages.size() > 2) {
            StringBuilder builder = new StringBuilder("Ответь на эти вопросы общими словами или проигнорируй:\n");

            int i = 1;
            for (Map.Entry<String, String> entry : translatedMessages.entrySet()) {
                builder.append(i++)
                        .append(". ")
                        .append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }

            translatedMessages.clear();
            processAnswer(new SystemMessage(builder.toString()));
            return;
        }

        translatedMessages.forEach((username, transcript) ->
                processAnswer(new UserMessage(username + ": " + transcript)));
        translatedMessages.clear();
    }

    private void processAnswer(Message message) {
        AssistantMessage assistantMessage = this.modelService.ask(message);

        if (modelsManager.getSynthesisModelState().isEnabled()) {
            byte[] speech = speechSynthesis.speech(assistantMessage.getText());
            audioProvider.addAudio(speech);
        }
    }

}
