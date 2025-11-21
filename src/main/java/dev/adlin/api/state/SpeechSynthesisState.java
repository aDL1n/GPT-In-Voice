package dev.adlin.api.state;

import org.springframework.stereotype.Component;

@Component
public class SpeechSynthesisState {

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
