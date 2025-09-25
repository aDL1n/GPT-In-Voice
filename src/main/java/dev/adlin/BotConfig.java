package dev.adlin;

import dev.adlin.service.LongTermMemoryService;
import dev.adlin.api.states.BotState;
import dev.adlin.api.states.util.BotStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    public BotState getBotState() {
        return new BotState(
                BotStatus.LOADING,
                null
        );
    }

    @Bean
    public Bot bot(@Value("${discord.token}") String token, @Value("${discord.guild-id}") String guildId, LongTermMemoryService longTermMemoryService, BotState botState) {
        return new Bot(token, guildId, longTermMemoryService, botState);
    }
}
