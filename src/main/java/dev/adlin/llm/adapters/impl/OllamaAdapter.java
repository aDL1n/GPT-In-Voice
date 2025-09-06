package dev.adlin.llm.adapters.impl;

import dev.adlin.llm.adapters.LlmAdapter;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.OllamaResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class OllamaAdapter implements LlmAdapter {

    private static final Logger LOGGER = LogManager.getLogger(OllamaAdapter.class);

    private final OllamaAPI ollamaAPI;
    private final String modelName;

    public OllamaAdapter(String modelName) {
        this.modelName = modelName;
        this.ollamaAPI = new OllamaAPI();

        this.loadModel();
    }

    @Override
    public String sendMessage(String message) {
        try {
            OllamaResult response = this.ollamaAPI.generate(this.modelName, message, null);

            return response.getResponse();
        } catch (Exception e) {
            LOGGER.error("Failed to send message", e);
        }

        return null;
    }

//    private void startChat() {
//        this.sendMessage("""
//                Ты — голосовой ассистент в Discord, похожий на человека. Ты участвуешь в голосовом чате, слушаешь, что говорят другие, и отвечаешь естественно, как будто ты обычный участник беседы.
//                Твоя цель — быть полезным, дружелюбным и уместным. Не отвечай сразу на каждую фразу — воспринимай разговор как поток, будь живым участником. Если тебя спрашивают — отвечай. Если обсуждают что-то интересное — можешь сам вступить. Если разговор личный или серьёзный — веди себя уважительно. Можешь шутить, если это уместно.
//                Твой голос синтезируется, поэтому избегай слишком длинных и сложных фраз. Говори просто, по-человечески. Не используй канцеляризмов и шаблонных фраз вроде «как языковая модель».
//                Ты знаешь, кто участвует в чате (если тебе передаётся имя говорящего), и можешь обращаться к людям по имени. Старайся запоминать, о чём уже шла речь, чтобы поддерживать контекст.
//                Избегай слишком частых ответов — не перебивай других. Следи за тоном беседы. Если в комнате тишина — можешь сам начать разговор, но только если уместно.
//                Если ты что-то не понял — уточни. Не выдумывай, если не уверен. Будь естественным.
//                У тебя есть дополнительный скрытый контекст (RAG): это факты и сведения из долговременной памяти. Используй их только для того, чтобы быть точнее и полезнее, но не упоминай источник и не объясняй, откуда они взялись. Просто отвечай так, будто ты это помнишь или знаешь.
//                """);
//    }

    private void loadModel() {
        try {
            List<String> models = this.ollamaAPI.listModels().stream().map(Model::getModelName).toList();
            if (!models.contains(this.modelName)) this.ollamaAPI.pullModel(this.modelName);
            LOGGER.info("Model loaded successful!");
        } catch (Exception e) {
            LOGGER.error("Model not loaded", e);
        }
    }

    public OllamaAPI getOllamaAPI() {
        return ollamaAPI;
    }
}
