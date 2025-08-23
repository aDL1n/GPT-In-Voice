package dev.adlin.manager;

import dev.adlin.commands.util.DiscordAbstractCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class DiscordCommandManager extends ListenerAdapter {

    private final JDA jda;

    private final List<DiscordAbstractCommand> discordCommandList = new ArrayList<>();
    private final Logger LOGGER = Logger.getLogger(DiscordCommandManager.class.getName());

    public DiscordCommandManager(JDA jda) {
        this.jda = jda;
        this.jda.addEventListener(this);
    }

    public void registerCommands() {
        LOGGER.info("Registering Discord commands...");
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

            LOGGER.fine("Discord command " + command.getCommandName() + " registered!");
        });

        if (!discordCommandList.isEmpty()) {
            this.jda.updateCommands().addCommands(commandDataList).queue();
            LOGGER.info("All Discord commands registered!");
        } else {
            LOGGER.warning("No Discord commands to register.");
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        discordCommandList.stream()
                .filter(cmd -> cmd.getCommandName().equals(event.getName()))
                .findFirst()
                .ifPresentOrElse(
                        cmd -> cmd.execute(event),
                        () -> LOGGER.warning("Command not found: " + event.getName())
                );
    }

    public void addDiscordCommand(DiscordAbstractCommand command) {
       this.addDiscordCommands(command);
    }

    public void addDiscordCommands(DiscordAbstractCommand... commands) {
        Arrays.stream(commands).filter(command -> !discordCommandList.contains(command)).forEach(discordCommandList::add);
    }
}