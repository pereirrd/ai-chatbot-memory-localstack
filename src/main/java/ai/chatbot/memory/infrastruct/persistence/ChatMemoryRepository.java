package ai.chatbot.memory.infrastruct.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ai.chatbot.memory.infrastruct.persistence.entity.ChatMemoryEntity;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
@RequiredArgsConstructor
public class ChatMemoryRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    public ChatMemoryEntity save(ChatMemoryEntity entity) {
        return dynamoDbTemplate.save(entity);
    }

    public Optional<ChatMemoryEntity> findById(String memoryId) {
        return Optional.ofNullable(
                dynamoDbTemplate.load(Key.builder().partitionValue(memoryId).build(), ChatMemoryEntity.class));
    }

    public void deleteById(String memoryId) {
        dynamoDbTemplate.delete(Key.builder().partitionValue(memoryId).build(), ChatMemoryEntity.class);
    }

    public boolean existsById(String memoryId) {
        return findById(memoryId).isPresent();
    }
}
