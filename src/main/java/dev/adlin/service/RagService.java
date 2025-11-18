package dev.adlin.service;

import dev.adlin.config.properties.RagConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.Arrays;
import java.util.List;

@Service
public class RagService {

    private static final Logger log = LogManager.getLogger(RagService.class);

    private final RagConfig ragConfig;
    private final VectorStore vectorStore;

    public RagService(RagConfig ragConfig, VectorStore vectorStore) {
        this.ragConfig = ragConfig;
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

    public void addMessages(Message... messages) {
        if (messages == null || messages.length == 0) return;

        List<Document> radData = Arrays.stream(messages)
                .filter(message -> message != null)
                .filter(message -> !message.getText().isBlank())
                .map(message -> new Document(message.getText(), message.getMetadata()))
                .toList();

        if (!radData.isEmpty()) this.vectorStore.add(radData);
    }

    public void addChatResponse(ChatResponse chatResponse) {
        List<AssistantMessage> assistantMessages = chatResponse.getResults().stream().map(Generation::getOutput).toList();

        List<Document> ragData = assistantMessages.stream()
                .filter(message -> !message.getText().isBlank())
                .map(message -> new Document(message.getText(), message.getMetadata()))
                .toList();

        if (!ragData.isEmpty()) this.vectorStore.add(ragData);
    }
}
