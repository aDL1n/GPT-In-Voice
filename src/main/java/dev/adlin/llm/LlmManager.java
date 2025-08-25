package dev.adlin.llm;

import dev.adlin.llm.adapters.OllamaAdapter;
import dev.adlin.llm.adapters.util.ILlmAdapter;
import dev.adlin.llm.adapters.util.Role;

public class LlmManager {

    private ILlmAdapter currentAdapter = new OllamaAdapter("llama3.2");

    public LlmManager() {
        currentAdapter.startChat();
    }

    public void sendFromSTT(String message) {
        System.out.println(currentAdapter.sendMessage(Role.USER, message));
    }

    public LlmManager setCurrentAdapter(ILlmAdapter adapter) {
        this.currentAdapter = adapter;
        return this;
    }

    public ILlmAdapter getCurrentAdapter() {
        return currentAdapter;
    }
}
