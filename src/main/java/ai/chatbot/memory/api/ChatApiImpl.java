package ai.chatbot.memory.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ai.chatbot.memory.application.ChatbotMemoryApp;
import ai.chatbot.memory.model.ChatRequest;
import ai.chatbot.memory.model.ChatResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatApiImpl implements ChatApi {

    private final ChatbotMemoryApp chatbotMemoryApp;

    @Override
    public ResponseEntity<ChatResponse> chat(ChatRequest chatRequest, UUID memoryId) {
        return ResponseEntity.ok(chatbotMemoryApp.chat(memoryId, chatRequest));
    }

}
