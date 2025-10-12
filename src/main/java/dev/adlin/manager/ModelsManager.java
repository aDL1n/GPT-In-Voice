package dev.adlin.manager;

import dev.adlin.config.SpeechRecognitionConfig;
import dev.adlin.config.SpeechSynthesisConfig;
import dev.adlin.speech.recognition.SpeechRecognition;
import dev.adlin.speech.synthesis.SpeechSynthesis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ModelsManager {

    private static final Logger log = LogManager.getLogger(ModelsManager.class);
    private final ConcurrentHashMap<String, SpeechSynthesis> synthesisModels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SpeechRecognition> recognitionModels = new ConcurrentHashMap<>();

    private String currentSynthesisModel;
    private String currentRecognitionModel;

    @Autowired
    public ModelsManager(List<SpeechSynthesis> synthesisList,
                         List<SpeechRecognition> recognitionList,
                         SpeechSynthesisConfig synthesisConfig,
                         SpeechRecognitionConfig recognitionConfig) {
        log.info("Initializing ModelsManager...");

        synthesisList.forEach(model -> synthesisModels.put(model.getName(), model));
        recognitionList.forEach(model -> recognitionModels.put(model.getName(), model));

        if (synthesisList.isEmpty()) throw new IllegalArgumentException("Synthesis models list is empty");
        if (recognitionList.isEmpty()) throw new IllegalArgumentException("Recognition models list is empty");

        currentSynthesisModel = synthesisConfig.getDefaultModel();
        log.info("Synthesis model set to {}", currentSynthesisModel);


        currentRecognitionModel = recognitionConfig.getDefaultModel();
        log.info("Recognition model set to {}", currentRecognitionModel);


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
        this.currentSynthesisModel = name;
        log.info("Synthesis model changed to {}", currentSynthesisModel);
        return synthesisModels.get(name);
    }

    public SpeechRecognition setCurrentRecognitionModel(String name) {
        this.currentRecognitionModel = name;
        log.info("Recognition model changed to {}", currentRecognitionModel);
        return recognitionModels.get(name);
    }
}