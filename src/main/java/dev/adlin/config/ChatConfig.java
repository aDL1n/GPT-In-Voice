package dev.adlin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("chat")
public class ChatConfig {
    private String ownerName;
    private int shortMemorySize;

    ChatConfig() {
        this.shortMemorySize = 30;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getShortMemorySize() {
        return shortMemorySize;
    }

    public void setShortMemorySize(int shortMemorySize) {
        this.shortMemorySize = shortMemorySize;
    }
}
