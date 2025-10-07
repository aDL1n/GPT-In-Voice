package dev.adlin.producer;

import dev.adlin.discord.audio.AudioBufferManager;
import dev.adlin.discord.audio.AudioProvider;
import dev.adlin.service.ModelService;
import dev.adlin.speech.recognition.SpeechRecognition;
import dev.adlin.speech.synthesis.SpeechSynthesis;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatProducer {

    private static final Logger log = LogManager.getLogger(ChatProducer.class);
    private final AudioBufferManager audioBufferManager;
    private final AudioProvider audioProvider;
    private final ModelService modelService;
    private final SpeechRecognition speechRecognition;
    private final SpeechSynthesis speechSynthesis;

    private final ConcurrentHashMap<User, String> translatedMessages = new ConcurrentHashMap<>();

    public ChatProducer(AudioBufferManager audioBufferManager,
                        AudioProvider audioProvider,
                        ModelService modelService,
                        SpeechRecognition speechRecognition,
                        SpeechSynthesis speechSynthesis

    ) {
        this.audioBufferManager = audioBufferManager;
        this.audioProvider = audioProvider;
        this.modelService = modelService;
        this.speechRecognition = speechRecognition;
        this.speechSynthesis = speechSynthesis;

        audioBufferManager.setBufferListener((user, data) -> {
            String userName = user.getName();

            String transcript = this.speechRecognition.transcriptAudio(data);

            UserMessage message = new UserMessage(userName + ": " + transcript);
            AssistantMessage assistantMessage = this.modelService.ask(message);

            byte[] speech = speechSynthesis.speech(assistantMessage.getText());

            audioProvider.addAudio(speech);

        });
    }

    @PostConstruct
    private void start() {

    }

}
