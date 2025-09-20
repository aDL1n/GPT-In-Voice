package dev.adlin;

import dev.adlin.service.LongTermMemoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Bot bot(@Value("${discord.token}") String token, @Value("${discord.guild-id}") String guildId, LongTermMemoryService longTermMemoryService) {
        return new Bot(token, guildId, longTermMemoryService);
    }
}