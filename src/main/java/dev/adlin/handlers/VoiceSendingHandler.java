package dev.adlin.handlers;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class VoiceSendingHandler implements AudioSendHandler {

    @Override
    public boolean canProvide() {
        return false;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return null;
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}
