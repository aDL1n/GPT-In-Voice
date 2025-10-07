package dev.adlin.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.function.Consumer;

public class JoinCommand extends DiscordAbstractCommand {

    private final Consumer<SlashCommandInteractionEvent> eventExecutor;

    public JoinCommand(Consumer<SlashCommandInteractionEvent> eventExecutor) {
        super("join", "send join request to bot", new OptionData(OptionType.USER, "user", "to user"));

        this.eventExecutor = eventExecutor;
    }

    @Override
    public void execute(SlashCommandInteractionEvent commandInteraction) {
        this.eventExecutor.accept(commandInteraction);
    }
}
