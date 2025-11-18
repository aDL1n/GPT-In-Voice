package dev.adlin.memory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SystemPromptLoader {

    private static final Logger log = LogManager.getLogger(SystemPromptLoader.class);

    public Message load() {
        log.info("Loading start prompt...");
        Path startPromptPath = Paths.get("src/main/resources/startPrompt.txt");

        try (Stream<String> lines = Files.lines(startPromptPath)) {
            log.info("Start prompt loaded successfully");
            return new SystemMessage(lines.collect(Collectors.joining("\n"))

            );
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file startPrompt.txt " + e);
        }
    }
}
