package dev.adlin.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DiscordVoiceListener extends ListenerAdapter {

    private final Consumer<GuildVoiceUpdateEvent> consumer;

    public DiscordVoiceListener(Consumer<GuildVoiceUpdateEvent> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        consumer.accept(event);
    }
}
