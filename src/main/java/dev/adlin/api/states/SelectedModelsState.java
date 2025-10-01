package dev.adlin.api.states;

import dev.adlin.llm.adapters.LlmAdapter;
import dev.adlin.llm.adapters.impl.OllamaAdapter;
import dev.adlin.stt.SpeechToText;
import dev.adlin.stt.impl.Whisper;
import dev.adlin.tts.TextToSpeech;
import dev.adlin.tts.impl.Piper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Bean;

public class SelectedModelsState {

    @Nullable
    private String llmModelName;
    @Nullable
    private String sttModelName;
    @Nullable
    private String ttsModelName;

    public SelectedModelsState() {}

    @Nullable
    public String getLlmModelName() {
        return llmModelName;
    }

    @Nullable
    public String getSttModelName() {
        return sttModelName;
    }

    @Nullable
    public String getTtsModelName() {
        return ttsModelName;
    }

    public void setLlmModelName(@Nullable String llmModelName) {
        this.llmModelName = llmModelName;
    }

    public void setSttModelName(@Nullable String sttModelName) {
        this.sttModelName = sttModelName;
    }

    public void setTtsModelName(@Nullable String ttsModelName) {
        this.ttsModelName = ttsModelName;
    }

    public void setLlmModelName(@NotNull LlmAdapter llmModel) {
        this.llmModelName = llmModel.getClass().getSimpleName();
    }

    public void setSttModelName(@NotNull SpeechToText sttModel) {
        this.sttModelName = sttModel.getClass().getSimpleName();
    }

    public void setTtsModelName(@NotNull TextToSpeech ttsModel) {
        this.ttsModelName = ttsModel.getClass().getSimpleName();
    }

    public void setAll(LlmAdapter llmAdapter, SpeechToText speechToText, TextToSpeech textToSpeech) {
        this.setLlmModelName(llmAdapter);
        this.setSttModelName(speechToText);
        this.setTtsModelName(textToSpeech);
    }
}
