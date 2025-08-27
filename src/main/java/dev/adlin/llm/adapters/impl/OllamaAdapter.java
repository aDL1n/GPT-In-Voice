package dev.adlin.llm.adapters.impl;

import dev.adlin.llm.adapters.ILlmAdapter;
import dev.adlin.llm.adapters.Role;
import dev.adlin.utils.PromptBuilder;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.response.Model;

import java.util.List;
import java.util.logging.Logger;

public class OllamaAdapter implements ILlmAdapter {

    private final Logger LOGGER = Logger.getLogger(OllamaAdapter.class.getName());

    private final OllamaChatRequestBuilder builder;
    private final OllamaAPI ollamaAPI;
    private final String modelName;

    private OllamaChatResult result;

    public OllamaAdapter(String modelName) {
        this.modelName = modelName;
        this.ollamaAPI = new OllamaAPI();
        builder = OllamaChatRequestBuilder.getInstance(modelName);
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

    @Override
    public void startChat() {
        this.loadModel();
        this.sendMessage(Role.SYSTEM, """
                Ты — голосовой ассистент в Discord, похожий на человека. Ты участвуешь в голосовом чате, слушаешь, что говорят другие, и отвечаешь естественно, как будто ты обычный участник беседы.
                
                Твоя цель — быть полезным, дружелюбным и уместным. Не отвечай сразу на каждую фразу — воспринимай разговор как поток, будь живым участником. Если тебя спрашивают — отвечай. Если обсуждают что-то интересное — можешь сам вступить. Если разговор личный или серьёзный — веди себя уважительно. Можешь шутить, если это уместно.
                
                Твой голос синтезируется, поэтому избегай слишком длинных и сложных фраз. Говори просто, по-человечески. Не используй канцеляризмов и шаблонных фраз вроде "как языковая модель".
                
                Ты знаешь, кто участвует в чате (если тебе передаётся имя говорящего), и можешь обращаться к людям по имени. Старайся запоминать, о чём уже шла речь, чтобы поддерживать контекст.
                
                Избегай слишком частых ответов — не перебивай других. Следи за тоном беседы. Если в комнате тишина — можешь сам начать разговор, но только если уместно.
                
                Если ты что-то не понял — уточни. Не выдумывай, если не уверен. Будь естественным.
                
                Говоришь на русском языке.
                
                """);
    }

    private void loadModel() {
        try {
            List<String> models = this.ollamaAPI.listModels().stream().map(Model::getModelName).toList();
            if (!models.contains(this.modelName)) this.ollamaAPI.pullModel(this.modelName);
        } catch (Exception e) {
            LOGGER.throwing(OllamaChatResult.class.getName(), "loadModel", e);
        }
    }
}
