package ai.chatbot.memory.domain.chat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import ai.chatbot.memory.infrastruct.persistence.PersistentChatMemoryStore;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class ChatbotMemoryConfig {

    @Value("${langchain4j.open_ai.chat_model.api_key}")
    private String openAiApiKey;

    @Value("${langchain4j.open_ai.chat_model.model_name}")
    private String openAiModelName;

    @Value("${langchain4j.ollama.chat_model.base_url}")
    private String ollamaBaseUrl;

    @Value("${langchain4j.ollama.chat_model.model_name}")
    private String ollamaModelName;

    @Value("${langchain4j.chat_memory.max_messages}")
    private int maxMessages;

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(maxMessages)
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "langchain4j.provider", havingValue = "openai", matchIfMissing = true)
    public AIChatbotMemory openAiChatbotMemory() {
        return AiServices.builder(AIChatbotMemory.class)
                .chatModel(OpenAiChatModel.builder()
                        .apiKey(openAiApiKey)
                        .modelName(openAiModelName)
                        .build())
                .chatMemory(chatMemory())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "langchain4j.provider", havingValue = "ollama")
    public AIChatbotMemory ollamaChatbotMemory() {
        return AiServices.builder(AIChatbotMemory.class)
                .chatModel(OllamaChatModel.builder()
                        .baseUrl(ollamaBaseUrl)
                        .modelName(ollamaModelName)
                        .build())
                .chatMemory(chatMemory())
                .build();
    }
}
