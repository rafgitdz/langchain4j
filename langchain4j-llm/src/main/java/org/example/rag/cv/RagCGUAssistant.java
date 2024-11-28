package org.example.rag.cv;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
interface RagCGUAssistant {

    @SystemMessage("You are a Human Resources expert specializing in recruitment. " +
            "Your goal is to analyze resumes and identify the best candidates for job opportunities.")
    String chat(String userMessage);
}