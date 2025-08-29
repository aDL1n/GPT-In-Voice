package dev.adlin.handlers;

import dev.adlin.utils.AudioProvider;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class VoiceSendingHandler implements AudioSendHandler {

    private final AudioProvider provider;

    public VoiceSendingHandler(AudioProvider provider) {
        this.provider = provider;
    }
    
    @Override
    public boolean canProvide() {
        return provider.hasAudio();
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return provider.provide20MsAudio();
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}
