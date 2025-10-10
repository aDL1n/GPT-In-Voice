package dev.adlin.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private static final Logger log = LogManager.getLogger(RagService.class);
    private final VectorStore vectorStore;

    public RagService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        log.info("RAG service initialized");
    }

    public String searchInMemory(String query) {
        log.info("Searching in memory");

        SearchRequest request = SearchRequest.builder()
                .topK(5)
                .query(query)
                .similarityThreshold(0.8)
                .build();

        List<Document> similarMemories = this.vectorStore.similaritySearch(request);

        StringBuilder response = new StringBuilder("Поиск по долгосрочной памяти: \n");
        for (int i = 0; i < similarMemories.size(); i++) {
            Document document = similarMemories.get(i);
            response.append(i + 1)
                    .append(": ")
                    .append(document.getText())
                    .append("\n");
        }

        return similarMemories.isEmpty() ? null : response.toString();
    }

    public void write(ChatResponse chatResponse) {
        List<AssistantMessage> assistantMessages = chatResponse.getResults().stream().map(Generation::getOutput).toList();

        List<Document> toRagData = assistantMessages.stream()
                .filter(message -> message.getText().isBlank())
                .map(message -> new Document(message.getText(), message.getMetadata()))
                .toList();

        this.vectorStore.add(toRagData);
    }
}
