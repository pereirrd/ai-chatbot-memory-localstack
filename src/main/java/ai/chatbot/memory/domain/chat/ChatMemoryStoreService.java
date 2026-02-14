package ai.chatbot.memory.domain.chat;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import ai.chatbot.memory.infrastruct.persistence.ChatMemoryRepository;
import ai.chatbot.memory.infrastruct.persistence.entity.ChatMemoryEntity;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.JacksonChatMessageJsonCodec;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMemoryStoreService implements ChatMemoryStore {

    private static final JacksonChatMessageJsonCodec CHAT_MESSAGE_CODEC = new JacksonChatMessageJsonCodec();

    private final ChatMemoryRepository chatMemoryRepository;

    @Override
    public void deleteMessages(Object memoryId) {
        chatMemoryRepository.deleteById(memoryId.toString());
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return chatMemoryRepository.findById(memoryId.toString())
                .map(entity -> parseMessages(entity.getMessages()))
                .orElse(Collections.emptyList());
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        var messagesJson = CHAT_MESSAGE_CODEC.messagesToJson(messages);
        var entity = ChatMemoryEntity.builder()
                .memoryId(memoryId.toString())
                .messages(messagesJson)
                .build();
        chatMemoryRepository.save(entity);
    }

    private List<ChatMessage> parseMessages(String messagesJson) {
        if (messagesJson == null || messagesJson.isBlank()) {
            return Collections.emptyList();
        }

        return CHAT_MESSAGE_CODEC.messagesFromJson(messagesJson);
    }
}
