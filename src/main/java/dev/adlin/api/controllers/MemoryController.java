package dev.adlin.api.controllers;

import dev.adlin.llm.memory.LongTermMemoryService;
import dev.adlin.llm.memory.entity.LongTermMemoryData;
import dev.adlin.llm.memory.entity.LongTermMemoryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memory")
public class MemoryController {

    private static final Logger log = LoggerFactory.getLogger(MemoryController.class);

    public final LongTermMemoryService longTermMemoryService;

    public MemoryController(LongTermMemoryService longTermMemoryService) {
        this.longTermMemoryService = longTermMemoryService;
    }

    @GetMapping("/long-term/all")
    public ResponseEntity<List<LongTermMemoryData>> getAllLongTermMemories() {
        log.info("REST request to get all LongTermMemoryData");
        return new ResponseEntity<>(this.longTermMemoryService.getAllLongTermMemories(), HttpStatus.OK);
    }

    @PostMapping("/long-term/create")
    public ResponseEntity<Void>  saveLongTermMemory(@RequestBody LongTermMemoryData longTermMemoryData) {
        this.longTermMemoryService.saveLongTermMemory(longTermMemoryData);

        log.info("REST request to save LongTermMemoryData : {}", longTermMemoryData);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
