[Português](README.pt-br.md) | [Español](README.es.md)

# AI Chatbot Memory

AI chatbot with conversation memory, using LangChain4j and DynamoDB for persistence.

## Technologies

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.4.5 |
| LangChain4j | 1.11.0-beta19 |
| Spring Cloud AWS (DynamoDB) | 3.0.2 |

## Infrastructure: LocalStack or AWS Cloud

The project supports local execution with **LocalStack** or production on **AWS Cloud**. Infrastructure is provisioned via **CloudFormation**.

To explore LocalStack, in addition to **DynamoDB** (chat memory persistence), the following were created:

| Resource | Purpose |
|----------|---------|
| **DynamoDB** | Conversation memory persistence |
| **S3** | Chat history storage |
| **Lambda** | Captures DynamoDB streams and forwards to S3 |

📖 **[Detailed deploy instructions →](localstack/README.md)**

## Model memory

Chat memory is managed by the LangChain4j `ChatMemoryStore` interface. The `ChatMemoryStoreService` implementation exposes read, update, and delete operations for messages, allowing the persistence backend to be swapped without changing the rest of the flow.

In this implementation, storage is done in **DynamoDB** through `ChatMemoryRepository`, which uses Spring Cloud AWS `DynamoDbTemplate`. The `ChatMemoryStoreService` is injected into `ChatMemoryProvider` and used by `MessageWindowChatMemory`, ensuring the model has access to previous conversation context in each session.

**Model interface** — the memory persistence identifier is defined by the `@MemoryId` annotation on the method parameter. LangChain4j uses this value to fetch and update history in the `ChatMemoryStore`:

```java
public interface AIChatbotMemory {

    @SystemMessage("Você é um assistente educado. Este será um chat informal sobre assuntos diversos, apenas uma conversa entre duas pessoas para passar o tempo.")
    String chat(@MemoryId UUID memoryId, @UserMessage String userMessage);
}
```

## AI Models: OpenAI or Ollama

You can use **OpenAI** (cloud) or **Ollama** (local) models:

| Provider | Variables |
|----------|-----------|
| `openai` | `OPENAI_API_KEY`, `OPENAI_MODEL_NAME` |
| `ollama` | `OLLAMA_BASE_URL`, `OLLAMA_MODEL_NAME` |

Configure `CHAT_MODEL_PROVIDER=openai` or `CHAT_MODEL_PROVIDER=ollama` in `.env`.

**OpenAI bean** (with persistence via `chatMemoryProvider`):

```java
@Bean
@ConditionalOnProperty(name = "langchain4j.provider", havingValue = "openai", matchIfMissing = true)
public AIChatbotMemory openAiChatbotMemory() {
    return AiServices.builder(AIChatbotMemory.class)
            .chatModel(OpenAiChatModel.builder()
                    .apiKey(openAiApiKey)
                    .modelName(openAiModelName)
                    .build())
            .chatMemoryProvider(chatMemoryProvider())
            .build();
}
```

**Ollama bean** (with persistence via `chatMemoryProvider`):

```java
@Bean
@ConditionalOnProperty(name = "langchain4j.provider", havingValue = "ollama")
public AIChatbotMemory ollamaChatbotMemory() {
    return AiServices.builder(AIChatbotMemory.class)
            .chatModel(OllamaChatModel.builder()
                    .baseUrl(ollamaBaseUrl)
                    .modelName(ollamaModelName)
                    .build())
            .chatMemoryProvider(chatMemoryProvider())
            .build();
}
```

**Persistence implementation** (`ChatMemoryProvider` injects the `ChatMemoryStore` in DynamoDB):

```java
private ChatMemoryProvider chatMemoryProvider() {
    return memoryId -> MessageWindowChatMemory.builder()
            .id(memoryId)
            .maxMessages(maxMessages)
            .chatMemoryStore(chatMemoryStoreService)
            .build();
}
```

## Request examples

**A Postman collection is available in the `resources` directory.**

**New conversation (without `memoryId`):**

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Qual a capital do Brasil?"}'
```

**Continue conversation (with `memoryId`):**

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -H "memoryId: 123e4567-e89b-12d3-a456-426614174000" \
  -d '{"message": "E qual é a população aproximada?"}'
```
