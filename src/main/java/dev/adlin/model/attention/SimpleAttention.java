package dev.adlin.model.attention;

import dev.adlin.config.properties.AttentionProperties;
import org.jetbrains.annotations.Nullable;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public final class SimpleAttention {

    private final boolean mentionOnly;
    @Nullable
    private final Pattern mentionPattern;

    public SimpleAttention(AttentionProperties properties) {
        this.mentionOnly = properties.isMentionOnly();

        if (!mentionOnly || properties.getMentions().isEmpty()) this.mentionPattern = null;
        else {
            String pattern = properties.getMentions().stream()
                    .map(String::trim)
                    .map(Pattern::quote)
                    .map(m -> "\\b" + m + "\\b")
                    .collect(Collectors.joining("|"));

            this.mentionPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        }
    }

    public boolean check(Message message) {
        if (!mentionOnly) return true;
        if (message.getText().isBlank() || mentionPattern == null) return false;

        return mentionPattern.matcher(message.getText()).find();
    }
}
