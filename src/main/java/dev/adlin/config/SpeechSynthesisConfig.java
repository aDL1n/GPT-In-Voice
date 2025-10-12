package dev.adlin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("speech.synthesis")
public class SpeechSynthesisConfig {
    private String url;
    private String defaultModel;

    SpeechSynthesisConfig() {
        this.url = "http://localhost:5002";
        this.defaultModel = "piper";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }
}
