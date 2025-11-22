package dev.adlin.config.properties;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.EnumSet;

@Configuration
@ConfigurationProperties("app.discord")
public class DiscordConfig {
    private String token;
    private String guildId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    @Bean
    public JDA getJda() {
        try {
            return JDABuilder.create(
                    getToken(),
                    EnumSet.of(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.MESSAGE_CONTENT
                    )
                    ).setActivity(Activity.customStatus("Waiting for you"))
                    .setStatus(OnlineStatus.IDLE)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public Guild getGuild() {
        Assert.notNull(getGuildId(),  "Guild Id cannot be null");
        return getJda().getGuildById(getGuildId());
    }
}
