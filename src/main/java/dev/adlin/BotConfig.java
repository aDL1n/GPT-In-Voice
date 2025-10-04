package dev.adlin;

import dev.adlin.api.state.DiscordState;
import dev.adlin.api.state.SelectedModelsState;
import dev.adlin.llm.memory.LongTermMemoryService;
import dev.adlin.api.state.BotState;
import dev.adlin.utils.ModelsFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    public Bot bot(@Value("${discord.token}") String token,
                   @Value("${discord.guild-id}") String guildId,
                   LongTermMemoryService longTermMemoryService,
                   BotState botState,
                   DiscordState discordState,
                   SelectedModelsState selectedModelsState,
                   ModelsFactory modelsFactory
    ) {
        return new Bot(
                token,
                guildId,
                longTermMemoryService,
                botState,
                discordState,
                selectedModelsState,
                modelsFactory
        );
    }
}
