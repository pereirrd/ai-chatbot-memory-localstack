package ai.chatbot.memory.config;

import org.springframework.stereotype.Component;

import ai.chatbot.memory.infrastruct.persistence.entity.ChatMemoryEntity;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMemoryTableNameResolver implements DynamoDbTableNameResolver {

    private final DynamoDbConfig dynamoDbConfig;

    @Override
    public <T> String resolve(Class<T> clazz) {
        if (ChatMemoryEntity.class.equals(clazz)) {
            return dynamoDbConfig.tableName();
        }

        throw new IllegalArgumentException("Unknown entity class: " + clazz.getName());
    }
}
