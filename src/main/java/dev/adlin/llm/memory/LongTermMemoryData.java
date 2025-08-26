package dev.adlin.llm.memory;

import dev.adlin.llm.adapters.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class LongTermMemoryData {
    @NotNull
    public Role role;
    @NotNull
    public Date date;
    @Nullable
    public String userName;
    @NotNull
    public String message;

    public LongTermMemoryData(@NotNull Role role, @NotNull Date date, @Nullable String userName, @NotNull String message) {
        this.role = role;
        this.date = date;
        this.userName = userName;
        this.message = message;
    }

    public LongTermMemoryData(@NotNull Role role, @NotNull Date date, @NotNull String message) {
        this(role, date, null, message);
    }
}
