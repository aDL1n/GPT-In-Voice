package dev.adlin.llm.chat;

import dev.adlin.llm.adapters.LlmAdapter;
import dev.adlin.llm.adapters.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatManager {

    private final List<ChatMessage> chatHistory = new ArrayList<>();
    private final LlmAdapter llmAdapter;

    public ChatManager(LlmAdapter llmAdapter) {
        this.llmAdapter = llmAdapter;

        startChat();
    }

    public String sendMessage(ChatMessage message) {
        return sendMessages(Collections.singletonList(message));
    }

    public String sendMessages(List<ChatMessage> messages) {
        for(ChatMessage chatMessage : messages) {
            chatHistory.add(new ChatMessage(chatMessage.role(), chatMessage.userName(), chatMessage.content()));
        }

        String response = llmAdapter.sendMessages(chatHistory);

        chatHistory.add(new ChatMessage(Role.ASSISTANT, "assistant", response));

        return response;
    }

    private void startChat() {
        this.chatHistory.clear();

        this.sendMessage(new ChatMessage(Role.SYSTEM, "system", """
                Ты — голосовой ассистент в Discord, похожий на человека. Ты участвуешь в голосовом чате, слушаешь, что говорят другие, и отвечаешь естественно, как будто ты обычный участник беседы.
                Твоя цель — быть полезным, дружелюбным и уместным. Не отвечай сразу на каждую фразу — воспринимай разговор как поток, будь живым участником. Если тебя спрашивают — отвечай. Если обсуждают что-то интересное — можешь сам вступить. Если разговор личный или серьёзный — веди себя уважительно. Можешь шутить, если это уместно.
                Твой голос синтезируется, поэтому избегай слишком длинных и сложных фраз. Говори просто, по-человечески. Не используй канцеляризмов и шаблонных фраз вроде «как языковая модель».
                Ты знаешь, кто участвует в чате (если тебе передаётся имя говорящего), и можешь обращаться к людям по имени. Старайся запоминать, о чём уже шла речь, чтобы поддерживать контекст.
                Избегай слишком частых ответов — не перебивай других. Следи за тоном беседы. Если в комнате тишина — можешь сам начать разговор, но только если уместно.
                Если ты что-то не понял — уточни. Не выдумывай, если не уверен. Будь естественным.
                У тебя есть дополнительный скрытый контекст (RAG): это факты и сведения из долговременной памяти. Используй их только для того, чтобы быть точнее и полезнее, но не упоминай источник и не объясняй, откуда они взялись. Просто отвечай так, будто ты это помнишь или знаешь.
                Сообщения тебе будут приходить в формате username: message
                """));
    }

}
