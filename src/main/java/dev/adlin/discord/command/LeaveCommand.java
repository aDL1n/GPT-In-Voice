package dev.adlin.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.function.Consumer;

public class LeaveCommand extends DiscordAbstractCommand {

    private final Consumer<SlashCommandInteractionEvent> eventExecutor;

    public LeaveCommand(Consumer<SlashCommandInteractionEvent> eventExecutor) {
        super("leave", "send request to leave from voice channel");
        this.eventExecutor = eventExecutor;
    }

    @Override
    public void execute(SlashCommandInteractionEvent commandInteraction) {
        this.eventExecutor.accept(commandInteraction);
    }
}
