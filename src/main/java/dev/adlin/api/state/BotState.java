package dev.adlin.api.state;

import dev.adlin.api.state.util.BotStatus;
import dev.adlin.llm.chat.ChatMessage;
import org.jetbrains.annotations.Nullable;

public class BotState {


    private BotStatus status = BotStatus.LOADING;
    @Nullable
    private ChatMessage currentPromptRequest;

    public BotState() {}

    public BotStatus getStatus() {
        return status;
    }

    public void setStatus(BotStatus status) {
        this.status = status;
    }

    public @Nullable ChatMessage getCurrentPromptRequest() {
        return currentPromptRequest;
    }

    public void setCurrentPromptRequest(@Nullable ChatMessage currentPromptRequest) {
        this.currentPromptRequest = currentPromptRequest;
    }
}
