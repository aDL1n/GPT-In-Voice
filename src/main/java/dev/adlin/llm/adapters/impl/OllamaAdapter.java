package dev.adlin.llm.adapters.impl;

import dev.adlin.llm.adapters.LlmAdapter;
import dev.adlin.utils.PromptUtils;
import dev.adlin.utils.chat.ChatMessage;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.response.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class OllamaAdapter implements LlmAdapter {

    private static final Logger LOGGER = LogManager.getLogger(OllamaAdapter.class);

    private final OllamaChatRequestBuilder builder;
    private final OllamaAPI ollamaAPI;
    private final String modelName;


    public OllamaAdapter(String modelName) {
        this.modelName = modelName;
        this.ollamaAPI = new OllamaAPI();
        builder = OllamaChatRequestBuilder.getInstance(modelName);

        this.loadModel();
    }

    @Override
    public String sendMessages(List<ChatMessage> messages) {
        OllamaChatRequest request = builder
                .withMessages(messages.stream().map(
                        message -> new OllamaChatMessage(PromptUtils.translateRole(message.role()), message.content())
                ).toList())
                .withKeepAlive("-1")
                .build();

        try {
            OllamaChatResult response = this.ollamaAPI.chat(request);
            System.out.println(response.getChatHistory().stream().map(OllamaChatMessage::getContent).collect(Collectors.joining("\n")));

            return response.getResponseModel().getMessage().getContent();
        } catch (Exception e) {
            LOGGER.error("Failed to send message", e);
        }

        return null;
    }

    private void loadModel() {
        try {
            List<String> models = this.ollamaAPI.listModels().stream().map(Model::getModelName).toList();
            if (!models.contains(this.modelName)) this.ollamaAPI.pullModel(this.modelName);
            LOGGER.info("Model loaded successful!");
        } catch (Exception e) {
            LOGGER.error("Model not loaded", e);
        }
    }

}