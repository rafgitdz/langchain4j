package org.example.rag.cgu;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
interface RagCVAssistant {

    String chat(String userMessage);
}