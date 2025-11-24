package dev.adlin.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("app.attention")
public class AttentionProperties {
    private List<String> mentions = List.of("GPT", "Бот", "Валера");
    private boolean mentionOnly = false;

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public boolean isMentionOnly() {
        return mentionOnly;
    }

    public void setMentionOnly(boolean mentionOnly) {
        this.mentionOnly = mentionOnly;
    }
}
