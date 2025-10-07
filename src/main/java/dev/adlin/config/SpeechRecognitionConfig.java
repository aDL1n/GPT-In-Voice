package dev.adlin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("speech.recognition")
public class SpeechRecognitionConfig {
    private String url;

    SpeechRecognitionConfig() {
        this.url = "http://localhost:5000";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
