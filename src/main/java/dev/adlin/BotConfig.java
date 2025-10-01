package dev.adlin;

import dev.adlin.api.states.DiscordState;
import dev.adlin.api.states.SelectedModelsState;
import dev.adlin.llm.memory.LongTermMemoryService;
import dev.adlin.api.states.BotState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    public BotState getBotState() {
        return new BotState();
    }

    @Bean
    public DiscordState getDiscordState() {
        return new DiscordState();
    }

    @Bean
    public SelectedModelsState getSelectedModelsState() {
        return new SelectedModelsState();
    }

    @Bean
    public Bot bot(@Value("${discord.token}") String token,
                   @Value("${discord.guild-id}") String guildId,
                   LongTermMemoryService longTermMemoryService,
                   BotState botState,
                   DiscordState discordState,
                   SelectedModelsState selectedModelsState) {
        return new Bot(token, guildId, longTermMemoryService, botState, discordState, selectedModelsState);
    }
}
