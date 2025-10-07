package dev.adlin.discord.listener;

import net.dv8tion.jda.api.entities.User;

public interface AudioBufferListener {
    void onBufferReady(User user, byte[] data);
}
