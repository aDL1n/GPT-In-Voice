package dev.adlin.llm.memory;

import dev.adlin.database.DataBase;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager {

    private final List<LongTermMemoryData> longTermMemory = new ArrayList<>();

    private final DataBase dataBase;

    public MemoryManager(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public void initializeLongTermMemory() {
        dataBase.createLongTermMemoryTable();
        dataBase.getLongTermMemories(100).thenAccept(longTermMemory::addAll);
    }

    public void addToLongTermMemory(LongTermMemoryData data) {
        longTermMemory.add(data);
        dataBase.saveLongTermMemory(data);
    }

    public List<LongTermMemoryData> getLongTermMemory() {
        return longTermMemory;
    }
}
