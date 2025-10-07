package dev.adlin.discord.audio;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

@Component
public class AudioProvider {
    private static final int SAMPLE_RATE = 48000;
    private static final int CHANNELS = 2;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int FRAME_SIZE = CHANNELS * BYTES_PER_SAMPLE;
    private static final int CHUNK_DURATION_MS = 20;
    private static final int CHUNK_SIZE = (SAMPLE_RATE * FRAME_SIZE * CHUNK_DURATION_MS) / 1000;

    private final Queue<byte[]> chunks = new LinkedList<>();

    public void addAudio(byte[] pcmData) {
        if (pcmData == null || pcmData.length == 0 ) return;

        int offset = 0;
        while (offset + CHUNK_SIZE <= pcmData.length) {
            byte[] chunk = new byte[CHUNK_SIZE];
            System.arraycopy(pcmData, offset, chunk, 0, CHUNK_SIZE);
            chunks.add(chunk);
            offset += CHUNK_SIZE;
        }
    }

    public ByteBuffer provide20MsAudio() {
        byte[] chunk = chunks.poll();
        return (chunk != null) ? ByteBuffer.wrap(chunk) : null;
    }

    public boolean hasAudio() {
        return !chunks.isEmpty();
    }
}
