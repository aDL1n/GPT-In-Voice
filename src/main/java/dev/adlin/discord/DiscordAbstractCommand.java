package dev.adlin.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DiscordAbstractCommand {

    private final String commandName;
    private final String description;
    private final boolean guildOnly;
    private final OptionData[] optionData;

    public DiscordAbstractCommand(@NotNull String commandName, @NotNull String description, boolean guildOnly, @Nullable OptionData... optionData) {
        this.commandName = commandName;
        this.description = description;
        this.guildOnly = guildOnly;
        this.optionData = optionData;
    }

    public DiscordAbstractCommand(@NotNull String commandName, @NotNull String description, boolean guildOnly) {
        this(commandName, description, guildOnly, null);
    }

    public DiscordAbstractCommand(@NotNull String commandName,@NotNull String description, @Nullable OptionData... optionData) {
        this(commandName, description, true, optionData);
    }

    public DiscordAbstractCommand(@NotNull String commandName, @NotNull String description) {
        this(commandName, description, true, null);
    }

    @NotNull
    public String getCommandName() {
        return commandName;
    }

    public abstract void execute(SlashCommandInteractionEvent commandInteraction);

    @NotNull
    public String getDescription() {
        return description;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    @Nullable
    public OptionData[] getOptionData() {
        return optionData;
    }
}