package ai.chatbot.memory.infrastruct.persistence;

import java.util.List;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

public class PersistentChatMemoryStore implements ChatMemoryStore {

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
