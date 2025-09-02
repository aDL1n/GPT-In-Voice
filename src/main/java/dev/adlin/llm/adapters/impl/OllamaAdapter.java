package dev.adlin.llm.adapters.impl;

import dev.adlin.llm.adapters.LlmAdapter;
import dev.adlin.llm.adapters.Role;
import dev.adlin.utils.PromptBuilder;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.response.Model;

import java.util.List;
import java.util.logging.Logger;

public class OllamaAdapter implements LlmAdapter {

    private final Logger LOGGER = Logger.getLogger(OllamaAdapter.class.getName());

    private final OllamaChatRequestBuilder builder;
    private final OllamaAPI ollamaAPI;
    private final String modelName;

    private OllamaChatResult result;

    public OllamaAdapter(String modelName) {
        this.modelName = modelName;
        this.ollamaAPI = new OllamaAPI();
        builder = OllamaChatRequestBuilder.getInstance(modelName);

        this.loadModel();
    }

    @Override
    public String sendMessage(Role role, String message) {
        OllamaChatRequest request;

        if (result != null) request = builder.withMessages(result.getChatHistory())
                .withMessage(PromptBuilder.translateRole(role), message)
                .build();
        else request = builder.withMessage(PromptBuilder.translateRole(role), message).build();

        try {
            result = ollamaAPI.chat(request);
            System.out.println(result.getResponseModel().getMessage().getContent());
            return result.getResponseModel().getMessage().getContent();

        } catch (Exception e) {
            LOGGER.throwing(OllamaChatResult.class.getName(), "sendMessage", e);
        }

        return null;
    }

    private void loadModel() {
        try {
            List<String> models = this.ollamaAPI.listModels().stream().map(Model::getModelName).toList();
            if (!models.contains(this.modelName)) this.ollamaAPI.pullModel(this.modelName);
        } catch (Exception e) {
            LOGGER.throwing(OllamaChatResult.class.getName(), "loadModel", e);
        }
    }

    public OllamaAPI getOllamaAPI() {
        return ollamaAPI;
    }
}
