package dev.adlin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("speech.synthesis")
public class SpeechSynthesisConfig {
    private String url;

    SpeechSynthesisConfig() {
        this.url = "http://localhost:5002";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
