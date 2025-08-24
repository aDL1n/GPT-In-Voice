package dev.adlin.llm;

import dev.adlin.llm.adapters.OllamaAdapter;
import dev.adlin.llm.adapters.util.ILlmAdapter;

public class LlmManager {

    private ILlmAdapter currentAdapter = new OllamaAdapter("llama3.2:1b");

    public LlmManager() {

    }

    public LlmManager setCurrentAdapter(ILlmAdapter adapter) {
        this.currentAdapter = adapter;
        return this;
    }

    public ILlmAdapter getCurrentAdapter() {
        return currentAdapter;
    }
}
