package dev.adlin.api.states;

import dev.adlin.api.states.util.BotStatus;
import dev.adlin.llm.chat.ChatMessage;

public class BotState {

    private BotStatus status;
    private ChatMessage currentPromptRequest;

    public BotState(BotStatus status, ChatMessage currentPromptRequest) {
        this.status = status;
        this.currentPromptRequest = currentPromptRequest;
    }

    public BotState() {
    }

    public BotStatus getStatus() {
        return status;
    }

    public void setStatus(BotStatus status) {
        this.status = status;
    }

    public ChatMessage getCurrentPromptRequest() {
        return currentPromptRequest;
    }

    public void setCurrentPromptRequest(ChatMessage currentPromptRequest) {
        this.currentPromptRequest = currentPromptRequest;
    }
}
