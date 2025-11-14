package dev.adlin.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.chat")
public class ChatConfig {
    private String ownerName = "aDL1n";
    private int shortMemorySize = 30;

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
