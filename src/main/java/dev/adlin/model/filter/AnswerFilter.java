package dev.adlin.model.filter;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class AnswerFilter {

    private static final Pattern FILTER_PATTERN =
            Pattern.compile("[^\\\\p{L}\\\\p{N}\\\\p{P}\\\\p{Z}]", Pattern.MULTILINE | Pattern.UNICODE_CASE);

    public String process(String text) {
        return FILTER_PATTERN.matcher(text).replaceAll("");
    }

}
