package dev.adlin.discord.listener;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class VoiceListener extends ListenerAdapter {
    private Consumer<GuildVoiceUpdateEvent> consumer;

    public VoiceListener() {}

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (consumer == null) return;
        consumer.accept(event);
    }

    public void setConsumer(Consumer<GuildVoiceUpdateEvent> consumer) {
        this.consumer = consumer;
    }
}
