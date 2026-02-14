package ai.chatbot.memory.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import ai.chatbot.memory.domain.chat.AIChatbotMemory;
import ai.chatbot.memory.domain.chat.ChatMemoryStoreService;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
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

	private final ChatMemoryStoreService chatMemoryStoreService;

	@Bean
	@ConditionalOnProperty(name = "langchain4j.provider", havingValue = "openai", matchIfMissing = true)
	public AIChatbotMemory openAiChatbotMemory() {
		log.info("Initializing OpenAI chatbot memory");

		return AiServices.builder(AIChatbotMemory.class)
				.chatModel(OpenAiChatModel.builder()
						.apiKey(openAiApiKey)
						.modelName(openAiModelName)
						.build())
				.chatMemoryProvider(chatMemoryProvider())
				.build();
	}

	@Bean
	@ConditionalOnProperty(name = "langchain4j.provider", havingValue = "ollama")
	public AIChatbotMemory ollamaChatbotMemory() {
		log.info("Initializing Ollama chatbot memory");

		return AiServices.builder(AIChatbotMemory.class)
				.chatModel(OllamaChatModel.builder()
						.baseUrl(ollamaBaseUrl)
						.modelName(ollamaModelName)
						.build())
				.chatMemoryProvider(chatMemoryProvider())
				.build();
	}

	private ChatMemoryProvider chatMemoryProvider() {
		return memoryId -> MessageWindowChatMemory.builder()
				.id(memoryId)
				.maxMessages(maxMessages)
				.chatMemoryStore(chatMemoryStoreService)
				.build();
	}
}
