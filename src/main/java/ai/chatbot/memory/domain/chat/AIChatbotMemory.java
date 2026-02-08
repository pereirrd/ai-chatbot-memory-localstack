package ai.chatbot.memory.domain.chat;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import java.util.UUID;

public interface AIChatbotMemory {

    @SystemMessage("Você é um assistente educado. Este será um chat informal sobre assuntos diversos, apenas uma conversa entre duas pessoas para passar o tempo.")
    String chat(@MemoryId UUID memoryId, @UserMessage String userMessage);
}
