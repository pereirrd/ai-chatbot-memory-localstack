package ai.chatbot.memory.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ai.chatbot.memory.domain.chat.AIChatbotMemory;
import ai.chatbot.memory.model.ChatRequest;
import ai.chatbot.memory.model.ChatResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotMemoryApp {

    private final AIChatbotMemory chatbotMemory;

    public ChatResponse chat(UUID memoryId, ChatRequest chatRequest) {
        if (memoryId == null) {
            memoryId = UUID.randomUUID();
        }

        var response = chatbotMemory.chat(memoryId, chatRequest.getMessage());

        return new ChatResponse().response(response).memoryId(memoryId);
    }

}
