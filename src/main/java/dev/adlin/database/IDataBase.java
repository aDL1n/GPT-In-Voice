package dev.adlin.database;

import dev.adlin.llm.memory.LongTermMemoryData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IDataBase {
    void load();
    void createLongTermMemoryTable();
    void saveLongTermMemory(LongTermMemoryData memoryData);
    void saveLongTermMemorys(List<LongTermMemoryData> memoryDataList);
    CompletableFuture<List<LongTermMemoryData>> getAllLongTermMemorys();
}
