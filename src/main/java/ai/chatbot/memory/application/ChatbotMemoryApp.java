package ai.chatbot.memory.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ai.chatbot.memory.model.ChatRequest;
import ai.chatbot.memory.model.ChatResponse;

@Service
public class ChatbotMemoryApp {

    public ChatResponse chat(ChatRequest chatRequest, UUID memoryId) {
        return new ChatResponse();
    }

}
