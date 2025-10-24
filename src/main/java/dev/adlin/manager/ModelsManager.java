package dev.adlin.manager;

import dev.adlin.api.state.RecognitionModelState;
import dev.adlin.api.state.SynthesisModelState;
import dev.adlin.config.SpeechRecognitionConfig;
import dev.adlin.config.SpeechSynthesisConfig;
import dev.adlin.speech.recognition.SpeechRecognition;
import dev.adlin.speech.synthesis.SpeechSynthesis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ModelsManager {

    private static final Logger log = LogManager.getLogger(ModelsManager.class);
    private final ConcurrentHashMap<String, SpeechSynthesis> synthesisModels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SpeechRecognition> recognitionModels = new ConcurrentHashMap<>();

    private String currentSynthesisModel;
    private String currentRecognitionModel;

    private final SynthesisModelState synthesisModelState;
    private final RecognitionModelState recognitionModelState;

    @Autowired
    public ModelsManager(List<SpeechSynthesis> synthesisList,
                         List<SpeechRecognition> recognitionList,
                         SpeechSynthesisConfig synthesisConfig,
                         SpeechRecognitionConfig recognitionConfig,
                         SynthesisModelState synthesisModelState,
                         RecognitionModelState recognitionModelState
    ) {

        this.synthesisModelState = synthesisModelState;
        this.recognitionModelState = recognitionModelState;
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
        if (!this.synthesisModels.containsKey(name)) throw new NoSuchElementException("Synthesis model not found");
        this.currentSynthesisModel = name;
        log.info("Synthesis model changed to {}", currentSynthesisModel);
        return synthesisModels.get(name);
    }

    public SpeechRecognition setCurrentRecognitionModel(String name) {
        if (!this.recognitionModels.containsKey(name)) throw new NoSuchElementException("Recognition model not found");
        this.currentRecognitionModel = name;
        log.info("Recognition model changed to {}", currentRecognitionModel);
        return recognitionModels.get(name);
    }

    public SynthesisModelState getSynthesisModelState() {
        return this.synthesisModelState;
    }

    public RecognitionModelState getRecognitionModelState() {
        return this.recognitionModelState;
    }

    public List<String> getSynthesisModelNames() {
        return synthesisModels.keySet().stream().collect(Collectors.toList());
    }

    public List<String> getRecognitionModelNames() {
        return recognitionModels.keySet().stream().collect(Collectors.toList());
    }
}