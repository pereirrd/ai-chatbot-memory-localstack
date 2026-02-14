# AI Chatbot Memory

Chatbot de IA com memória de conversação, utilizando LangChain4j e DynamoDB para persistência.

## Tecnologias

| Tecnologia | Versão |
|------------|--------|
| Java | 21 |
| Spring Boot | 3.4.5 |
| LangChain4j | 1.11.0-beta19 |
| Spring Cloud AWS (DynamoDB) | 3.0.2 |

## Infraestrutura: LocalStack ou AWS Cloud

O projeto suporta execução local com **LocalStack** ou em produção na **AWS Cloud**. A infraestrutura é provisionada via **CloudFormation**.

Para explorar o LocalStack, além do **DynamoDB** (persistência da memória do chat), foram criados:

| Recurso | Função |
|---------|--------|
| **DynamoDB** | Persistência da memória de conversação |
| **S3** | Armazenamento do histórico de conversas |
| **Lambda** | Captura streams do DynamoDB e encaminha para o S3 |

📖 **[Instruções detalhadas de deploy →](localstack/README.md)**

## Memória do modelo

A memória do chat é gerenciada pela interface `ChatMemoryStore` do LangChain4j. A implementação `ChatMemoryStoreService` expõe as operações de leitura, atualização e exclusão de mensagens, permitindo trocar o backend de persistência sem alterar o restante do fluxo.

Nesta implementação, o armazenamento é feito em **DynamoDB** por meio do `ChatMemoryRepository`, que utiliza o `DynamoDbTemplate` do Spring Cloud AWS. O `ChatMemoryStoreService` é injetado no `ChatMemoryProvider` e usado pelo `MessageWindowChatMemory`, garantindo que o modelo tenha acesso ao contexto das conversas anteriores em cada sessão.

**Interface do modelo** — o identificador de persistência da memória é definido pela anotação `@MemoryId` no parâmetro do método. O LangChain4j utiliza esse valor para buscar e atualizar o histórico no `ChatMemoryStore`:

```java
public interface AIChatbotMemory {

    @SystemMessage("Você é um assistente educado. Este será um chat informal sobre assuntos diversos, apenas uma conversa entre duas pessoas para passar o tempo.")
    String chat(@MemoryId UUID memoryId, @UserMessage String userMessage);
}
```

## Modelos de IA: OpenAI ou Ollama

É possível usar modelos da **OpenAI** (cloud) ou **Ollama** (local):

| Provider | Variáveis |
|----------|-----------|
| `openai` | `OPENAI_API_KEY`, `OPENAI_MODEL_NAME` |
| `ollama` | `OLLAMA_BASE_URL`, `OLLAMA_MODEL_NAME` |

Configure `CHAT_MODEL_PROVIDER=openai` ou `CHAT_MODEL_PROVIDER=ollama` no `.env`.

**Bean OpenAI** (com persistência via `chatMemoryProvider`):

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

**Bean Ollama** (com persistência via `chatMemoryProvider`):

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

**Implementação da persistência** (`ChatMemoryProvider` injeta o `ChatMemoryStore` em DynamoDB):

```java
private ChatMemoryProvider chatMemoryProvider() {
    return memoryId -> MessageWindowChatMemory.builder()
            .id(memoryId)
            .maxMessages(maxMessages)
            .chatMemoryStore(chatMemoryStoreService)
            .build();
}
```

## Exemplos de requisição

**No diretório `resources` tem uma collection que pode ser utilizado no Postman.**

**Nova conversa (sem `memoryId`):**

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Qual a capital do Brasil?"}'
```

**Continuar conversa (com `memoryId`):**

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -H "memoryId: 123e4567-e89b-12d3-a456-426614174000" \
  -d '{"message": "E qual é a população aproximada?"}'
```
