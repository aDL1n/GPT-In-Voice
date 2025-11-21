package dev.adlin.api.state;

import org.springframework.stereotype.Component;

@Component
public class SpeechRecognitionState {

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
