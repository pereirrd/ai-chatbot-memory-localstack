package ai.chatbot.memory.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ai.chatbot.memory.model.ChatRequest;
import ai.chatbot.memory.model.ChatResponse;

@RestController
public class ChatApiImpl implements ChatApi {

    @Override
    public ResponseEntity<ChatResponse> chat(ChatRequest chatRequest, UUID memoryId) {
        return ResponseEntity.ok(new ChatResponse());
    }

}
