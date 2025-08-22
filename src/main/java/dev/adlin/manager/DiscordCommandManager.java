package dev.adlin.commands.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordCommandManager extends ListenerAdapter {

    private final JDA jda;

    private final List<DiscordAbstractCommand> discordCommandList = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DiscordCommandManager(JDA jda) {
        this.jda = jda;
        this.jda.addEventListener(this);
    }

    public void registerCommands() {
        LOGGER.warn("Registering Discord commands...");
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

            LOGGER.info("Discord command {} registered!", command.getCommandName());
        });

        if (!discordCommandList.isEmpty()) {
            this.jda.updateCommands().addCommands(commandDataList).queue();
            LOGGER.info("All Discord commands registered!");
        } else {
            LOGGER.warn("Discord commands is null!");
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        discordCommandList.stream().filter(command -> command.getCommandName().equals(event.getName())).findFirst().get().execute(event);
    }

    public void addDiscordCommand(DiscordAbstractCommand command) {
       this.addDiscordCommands(command);
    }
    public void addDiscordCommands(DiscordAbstractCommand... commands) {
        Arrays.stream(commands).filter(command -> !discordCommandList.contains(command)).forEach(discordCommandList::add);
    }
}