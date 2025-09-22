package dev.adlin.utils;

public class BotState {

    private BotStatus status;
    private String currentPromptRequest;

    public BotState(BotStatus status, String currentPromptRequest) {
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

    public String getCurrentPromptRequest() {
        return currentPromptRequest;
    }

    public void setCurrentPromptRequest(String currentPromptRequest) {
        this.currentPromptRequest = currentPromptRequest;
    }
}
