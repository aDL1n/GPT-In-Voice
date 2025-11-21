package dev.adlin.manager;

import dev.adlin.api.state.SpeechRecognitionState;
import dev.adlin.api.state.SpeechSynthesisState;
import dev.adlin.config.properties.SpeechRecognitionConfig;
import dev.adlin.config.properties.SpeechSynthesisConfig;
import dev.adlin.speech.recognition.SpeechRecognition;
import dev.adlin.speech.synthesis.SpeechSynthesis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ModelsManager {

    private static final Logger log = LogManager.getLogger(ModelsManager.class);
    private final ConcurrentHashMap<String, SpeechSynthesis> synthesisModels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SpeechRecognition> recognitionModels = new ConcurrentHashMap<>();

    private String currentSynthesisModel;
    private String currentRecognitionModel;

    private final SpeechSynthesisState speechSynthesisState;
    private final SpeechRecognitionState speechRecognitionState;

    @Autowired
    public ModelsManager(List<SpeechSynthesis> synthesisList,
                         List<SpeechRecognition> recognitionList,
                         SpeechSynthesisConfig synthesisConfig,
                         SpeechRecognitionConfig recognitionConfig,
                         SpeechSynthesisState speechSynthesisState,
                         SpeechRecognitionState speechRecognitionState
    ) {

        this.speechSynthesisState = speechSynthesisState;
        this.speechRecognitionState = speechRecognitionState;
        log.info("Initializing ModelsManager...");

        synthesisList.forEach(model -> synthesisModels.put(model.getName(), model));
        recognitionList.forEach(model -> recognitionModels.put(model.getName(), model));

        if (synthesisList.isEmpty()) throw new IllegalArgumentException("Synthesis models list is empty");
        if (recognitionList.isEmpty()) throw new IllegalArgumentException("Recognition models list is empty");

        currentSynthesisModel = synthesisConfig.getDefaultModel();
        log.info("Default synthesis model set to {}", currentSynthesisModel);


        currentRecognitionModel = recognitionConfig.getDefaultModel();
        log.info("Default recognition model set to {}", currentRecognitionModel);


        log.info("ModelsManager initialized");
    }

    public SpeechSynthesis getSpeechSynthesisModel() {
        if (currentSynthesisModel == null) throw new IllegalStateException("Synthesis model is null");
        return synthesisModels.get(currentSynthesisModel);
    }

    public SpeechRecognition getSpeechRecognitionModel() {
        if (currentRecognitionModel == null) throw new IllegalStateException("Recognition model is null");
        return recognitionModels.get(currentRecognitionModel);
    }

    public SpeechSynthesis setCurrentSynthesisModel(String name) {
        if (!this.synthesisModels.containsKey(name.toLowerCase())) throw new NoSuchElementException("Synthesis model not found");
        this.currentSynthesisModel = name.toLowerCase();
        log.info("Synthesis model changed to {}", currentSynthesisModel);
        return synthesisModels.get(name.toLowerCase());
    }

    public SpeechRecognition setCurrentRecognitionModel(String name) {
        if (!this.recognitionModels.containsKey(name.toLowerCase())) throw new NoSuchElementException("Recognition model not found");
        this.currentRecognitionModel = name.toLowerCase();
        log.info("Recognition model changed to {}", currentRecognitionModel);
        return recognitionModels.get(name.toLowerCase());
    }

    public SpeechSynthesisState getSpeechSynthesisState() {
        return this.speechSynthesisState;
    }

    public SpeechRecognitionState getSpeechRecognitionState() {
        return this.speechRecognitionState;
    }

    public List<String> getSynthesisModelNames() {
        return new ArrayList<>(synthesisModels.keySet());
    }

    public List<String> getRecognitionModelNames() {
        return new ArrayList<>(recognitionModels.keySet());
    }
}