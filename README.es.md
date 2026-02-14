[English](README.md) | [Português](README.pt-br.md)

# AI Chatbot Memory

Chatbot de IA con memoria de conversación, utilizando LangChain4j y DynamoDB para persistencia.

## Tecnologías

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.4.5 |
| LangChain4j | 1.11.0-beta19 |
| Spring Cloud AWS (DynamoDB) | 3.0.2 |

## Infraestructura: LocalStack o AWS Cloud

El proyecto soporta ejecución local con **LocalStack** o en producción en **AWS Cloud**. La infraestructura se provisiona mediante **CloudFormation**.

Para explorar LocalStack, además de **DynamoDB** (persistencia de la memoria del chat), se crearon:

| Recurso | Función |
|---------|---------|
| **DynamoDB** | Persistencia de la memoria de conversación |
| **S3** | Almacenamiento del historial de conversaciones |
| **Lambda** | Captura streams del DynamoDB y los envía al S3 |

📖 **[Instrucciones detalladas de deploy →](localstack/README.es.md)**

## Memoria del modelo

La memoria del chat se gestiona mediante la interfaz `ChatMemoryStore` de LangChain4j. La implementación `ChatMemoryStoreService` expone las operaciones de lectura, actualización y eliminación de mensajes, permitiendo cambiar el backend de persistencia sin alterar el resto del flujo.

En esta implementación, el almacenamiento se realiza en **DynamoDB** a través de `ChatMemoryRepository`, que utiliza el `DynamoDbTemplate` de Spring Cloud AWS. El `ChatMemoryStoreService` se inyecta en el `ChatMemoryProvider` y es utilizado por `MessageWindowChatMemory`, garantizando que el modelo tenga acceso al contexto de conversaciones anteriores en cada sesión.

**Interfaz del modelo** — el identificador de persistencia de la memoria se define mediante la anotación `@MemoryId` en el parámetro del método. LangChain4j utiliza este valor para buscar y actualizar el historial en el `ChatMemoryStore`:

```java
public interface AIChatbotMemory {

    @SystemMessage("Você é um assistente educado. Este será um chat informal sobre assuntos diversos, apenas uma conversa entre duas pessoas para passar o tempo.")
    String chat(@MemoryId UUID memoryId, @UserMessage String userMessage);
}
```

## Modelos de IA: OpenAI u Ollama

Es posible usar modelos de **OpenAI** (cloud) u **Ollama** (local):

| Provider | Variables |
|----------|-----------|
| `openai` | `OPENAI_API_KEY`, `OPENAI_MODEL_NAME` |
| `ollama` | `OLLAMA_BASE_URL`, `OLLAMA_MODEL_NAME` |

Configura `CHAT_MODEL_PROVIDER=openai` o `CHAT_MODEL_PROVIDER=ollama` en `.env`.

**Bean OpenAI** (con persistencia vía `chatMemoryProvider`):

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

**Bean Ollama** (con persistencia vía `chatMemoryProvider`):

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

**Implementación de la persistencia** (`ChatMemoryProvider` inyecta el `ChatMemoryStore` en DynamoDB):

```java
private ChatMemoryProvider chatMemoryProvider() {
    return memoryId -> MessageWindowChatMemory.builder()
            .id(memoryId)
            .maxMessages(maxMessages)
            .chatMemoryStore(chatMemoryStoreService)
            .build();
}
```

## Ejemplos de petición

**En el directorio `resources` hay una colección que puede utilizarse en Postman.**

**Nueva conversación (sin `memoryId`):**

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Qual a capital do Brasil?"}'
```

**Continuar conversación (con `memoryId`):**

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -H "memoryId: 123e4567-e89b-12d3-a456-426614174000" \
  -d '{"message": "E qual é a população aproximada?"}'
```
