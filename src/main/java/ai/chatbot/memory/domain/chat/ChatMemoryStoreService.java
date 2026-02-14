package ai.chatbot.memory.domain.chat;

import java.util.List;

import ai.chatbot.memory.infrastruct.persistence.ChatMemoryRepository;
import lombok.RequiredArgsConstructor;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

@RequiredArgsConstructor
public class ChatMemoryStoreService implements ChatMemoryStore {

    private final ChatMemoryRepository chatMemoryRepository;

    @Override
    public void deleteMessages(Object memoryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteMessages'");
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return List.of();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMessages'");
    }

}
