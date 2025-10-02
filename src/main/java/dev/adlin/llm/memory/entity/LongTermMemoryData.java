package dev.adlin.llm.memory.entity;

import dev.adlin.llm.adapter.Role;
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
    public String content;

    public LongTermMemoryData(@NotNull Role role, @NotNull Date date, @Nullable String userName, @NotNull String content) {
        this.role = role;
        this.date = date;
        this.userName = userName;
        this.content = content;
    }

    public LongTermMemoryData(@NotNull Role role, @NotNull Date date, @NotNull String content) {
        this(role, date, null, content);
    }
}
