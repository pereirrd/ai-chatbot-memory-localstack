package ai.chatbot.memory.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import ai.chatbot.memory.domain.chat.AIChatbotMemory;
import ai.chatbot.memory.domain.chat.ChatMemoryStoreService;
import ai.chatbot.memory.infrastruct.persistence.ChatMemoryRepository;
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
	@ConditionalOnProperty(name = "langchain4j.provider", havingValue = "openai", matchIfMissing = true)
	public AIChatbotMemory openAiChatbotMemory(ChatMemoryRepository chatMemoryRepository) {
		return AiServices.builder(AIChatbotMemory.class)
				.chatModel(OpenAiChatModel.builder()
						.apiKey(openAiApiKey)
						.modelName(openAiModelName)
						.build())
				.chatMemory(chatMemory(chatMemoryRepository))
				.build();
	}

	@Bean
	@ConditionalOnProperty(name = "langchain4j.provider", havingValue = "ollama")
	public AIChatbotMemory ollamaChatbotMemory(ChatMemoryRepository chatMemoryRepository) {
		return AiServices.builder(AIChatbotMemory.class)
				.chatModel(OllamaChatModel.builder()
						.baseUrl(ollamaBaseUrl)
						.modelName(ollamaModelName)
						.build())
				.chatMemory(chatMemory(chatMemoryRepository))
				.build();
	}

	private ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
		var persistentChatMemoryStore = new ChatMemoryStoreService(chatMemoryRepository);
		return MessageWindowChatMemory.builder()
				.maxMessages(maxMessages)
				.chatMemoryStore(persistentChatMemoryStore)
				.build();
	}
}
