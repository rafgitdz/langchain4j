package org.example.rag;

import dev.langchain4j.service.spring.AiService;

@AiService
interface RAGAssistant {

    String chat(String userMessage);
}