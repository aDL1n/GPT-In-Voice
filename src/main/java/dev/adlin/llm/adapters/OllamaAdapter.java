package dev.adlin.llm.adapters;

import dev.adlin.llm.adapters.util.ILlmAdapter;
import dev.adlin.llm.adapters.util.Role;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OllamaAdapter implements ILlmAdapter {

    private final Logger LOGGER = Logger.getLogger(OllamaAdapter.class.getName());

    private final OllamaChatRequestBuilder builder;
    private final OllamaAPI ollamaAPI;

    public OllamaAdapter(String modelName) {
        this.ollamaAPI = new OllamaAPI();
        builder = OllamaChatRequestBuilder.getInstance(modelName);
    }

    @Override
    public void sendMessage(Role role, String message) {
        OllamaChatRequest request = builder.withMessage(translateRole(role), message).build();
        try {
            OllamaChatResult result = ollamaAPI.chat(request);
            System.out.println(result.getResponseModel().getMessage().getContent());
        } catch (OllamaBaseException | IOException | InterruptedException | ToolInvocationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public void startChat() {

    }

    private OllamaChatMessageRole translateRole(Role role) {
        return switch (role) {
            case TOOL -> OllamaChatMessageRole.TOOL;
            case SYSTEM -> OllamaChatMessageRole.SYSTEM;
            case ASSISTANT -> OllamaChatMessageRole.ASSISTANT;
            case USER -> OllamaChatMessageRole.USER;
        };
    }
}
