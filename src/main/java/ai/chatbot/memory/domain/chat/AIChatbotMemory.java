package ai.chatbot.memory.domain.chat;

import dev.langchain4j.service.SystemMessage;

public interface AIChatbotMemory {

    @SystemMessage("You are a polite assistant")
    String chat(String message);
}
