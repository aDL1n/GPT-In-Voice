package dev.adlin.service;

import dev.adlin.llm.memory.LongTermMemoryData;
import dev.adlin.repository.LongTermMemoryRepository;
import dev.adlin.repository.entity.LongTermMemoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LongTermMemoryService {

    private final LongTermMemoryRepository longTermMemoryRepository;

    @Autowired
    public LongTermMemoryService(LongTermMemoryRepository longTermMemoryRepository) {
        this.longTermMemoryRepository = longTermMemoryRepository;
    }

    public void saveLongTermMemory(LongTermMemoryData longTermMemoryData) {
        LongTermMemoryEntity longTermMemoryEntity = new LongTermMemoryEntity(
                null,
                longTermMemoryData.role,
                longTermMemoryData.content,
                longTermMemoryData.userName,
                longTermMemoryData.date
        );

        longTermMemoryRepository.save(longTermMemoryEntity);
    }

    public void saveLongTermMemories(List<LongTermMemoryData> longTermMemories) {
        for (LongTermMemoryData longTermMemoryData : longTermMemories) {
            saveLongTermMemory(longTermMemoryData);
        }
    }

    public List<LongTermMemoryData> getAllLongTermMemories() {
        List<LongTermMemoryEntity> longTermMemoryEntityList = longTermMemoryRepository.findAll();

        return longTermMemoryEntityList.stream()
                .map(this::toRepoEntity)
                .collect(Collectors.toList());
    }

    public List<LongTermMemoryData> getLongTermMemories(int count) {
        List<LongTermMemoryEntity> longTermMemoryEntityList = longTermMemoryRepository.findLongTermMemory(count);

        return longTermMemoryEntityList.stream()
                .map(this::toRepoEntity)
                .collect(Collectors.toList());
    }

    private LongTermMemoryData toRepoEntity(LongTermMemoryEntity longTermMemoryEntity) {
        return new LongTermMemoryData(
                longTermMemoryEntity.getRole(),
                longTermMemoryEntity.getDate(),
                longTermMemoryEntity.getUsername(),
                longTermMemoryEntity.getContent()
        );
    }


}
