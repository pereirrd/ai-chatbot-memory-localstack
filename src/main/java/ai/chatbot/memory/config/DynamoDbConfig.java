package ai.chatbot.memory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chat.memory.dynamodb")
public record DynamoDbConfig(
        String endpoint,
        String region,
        String tableName
) {
}
