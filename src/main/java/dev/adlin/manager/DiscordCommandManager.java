package dev.adlin.manager;

import dev.adlin.Bot;
import dev.adlin.commands.util.DiscordAbstractCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordCommandManager extends ListenerAdapter {

    private final JDA jda;

    private final List<DiscordAbstractCommand> discordCommandList = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(DiscordCommandManager.class);

    public DiscordCommandManager(JDA jda) {
        this.jda = jda;
        this.jda.addEventListener(this);
    }

    public void registerCommands() {
        LOGGER.info("Registering commands...");
        List<SlashCommandData> commandDataList = new ArrayList<>();
        discordCommandList.forEach(command -> {
            if (command.getOptionData() != null) {
                commandDataList.add(
                        Commands.slash(command.getCommandName(), command.getDescription())
                                .setContexts(
                                        command.isGuildOnly() ? Collections.singletonList(InteractionContextType.GUILD)
                                                : InteractionContextType.ALL
                                )
                                .addOptions(command.getOptionData())
                );
            } else {
                commandDataList.add(
                        Commands.slash(command.getCommandName(), command.getDescription())
                                .setContexts(
                                        command.isGuildOnly() ? Collections.singletonList(InteractionContextType.GUILD)
                                                : InteractionContextType.ALL
                                )
                );
            }

            LOGGER.info("Command {} registered!", command.getCommandName());
        });

        if (!discordCommandList.isEmpty()) {
            this.jda.updateCommands().addCommands(commandDataList).queue();
            LOGGER.info("All Discord commands registered!");
        } else {
            LOGGER.warn("No Discord commands to register.");
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        discordCommandList.stream()
                .filter(command -> command.getCommandName().equals(event.getName()))
                .findFirst()
                .ifPresentOrElse(
                        command -> command.execute(event),
                        () -> LOGGER.warn("Command not found: {}", event.getName())
                );
    }

    public void addDiscordCommand(DiscordAbstractCommand command) {
       this.addDiscordCommands(command);
    }

    public void addDiscordCommands(DiscordAbstractCommand... commands) {
        Arrays.stream(commands)
                .filter(command -> !discordCommandList.contains(command))
                .forEach(discordCommandList::add);
    }
}