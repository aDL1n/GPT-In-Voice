package dev.adlin.llm.adapter;

public enum Role {
    USER("U"),
    SYSTEM("S"),
    TOOL("T"),
    ASSISTANT("A");

    final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static class Utils {
        public static Role getRoleFromDB(String dbRole) {
            return switch (dbRole) {
                case "S" -> Role.SYSTEM;
                case "T" -> Role.TOOL;
                case "A" -> Role.ASSISTANT;
                default -> Role.USER;
            };
        }
    }
}
