package dev.adlin.llm.memory;

import dev.adlin.llm.memory.entity.LongTermMemoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LongTermMemoryRepository extends JpaRepository<LongTermMemoryEntity, Long> {

    @Query(value = "select * from long_term limit :count", nativeQuery = true)
    List<LongTermMemoryEntity> findLongTermMemories(int count);
}
