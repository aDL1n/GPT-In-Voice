package dev.adlin.producer;

import dev.adlin.discord.audio.AudioBufferManager;
import dev.adlin.discord.audio.AudioProvider;
import dev.adlin.service.ModelService;
import dev.adlin.speech.recognition.SpeechRecognition;
import dev.adlin.speech.synthesis.SpeechSynthesis;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class ChatProducer {

    private static final Logger log = LogManager.getLogger(ChatProducer.class);
    private final AudioBufferManager audioBufferManager;
    private final AudioProvider audioProvider;
    private final ModelService modelService;
    private final SpeechSynthesis speechSynthesis;

    private final ConcurrentHashMap<String, String> translatedMessages = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private final static String OWNER_NAME = "aDL1n";

    public ChatProducer(AudioBufferManager audioBufferManager,
                        AudioProvider audioProvider,
                        ModelService modelService,
                        SpeechRecognition speechRecognition,
                        SpeechSynthesis speechSynthesis

    ) {
        this.audioBufferManager = audioBufferManager;
        this.audioProvider = audioProvider;
        this.modelService = modelService;
        this.speechSynthesis = speechSynthesis;

        audioBufferManager.setBufferListener((user, data) ->
                CompletableFuture.runAsync(() -> {
                    String transcript = speechRecognition.transcriptAudio(data);
                    translatedMessages.put(user.getName(), transcript);
                })
        );
    }

    @PostConstruct
    private void start() {
        scheduledExecutorService.schedule(() -> {
            if (translatedMessages.isEmpty()) return;

            if (translatedMessages.containsKey(OWNER_NAME)) {
                this.processAnswer(new UserMessage(OWNER_NAME + ": " + translatedMessages.get("aDL1n")));
                translatedMessages.remove(OWNER_NAME);
            } else if (translatedMessages.size() > 3) {
                StringBuilder builder = new StringBuilder();
                builder.append("Ответь на эти вопросы общими словами или проигнорируй\n");

                translatedMessages.forEach((username, transcript) -> {
                    builder.append(username)
                            .append(": ")
                            .append(transcript)
                            .append("\n");
                    translatedMessages.remove(username);
                });

                SystemMessage systemMessage = new SystemMessage(builder.toString());
                processAnswer(systemMessage);

            } else {
                translatedMessages.forEach((username, transcript) ->
                        processAnswer(new UserMessage(username + ": " + transcript)));
                translatedMessages.clear();
            }

        }, 1, TimeUnit.SECONDS);
    }

    private void processAnswer(Message message) {
        AssistantMessage assistantMessage = this.modelService.ask(message);

        byte[] speech = speechSynthesis.speech(assistantMessage.getText());
        audioProvider.addAudio(speech);
    }

}
