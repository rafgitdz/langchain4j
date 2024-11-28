package org.example.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
interface Assistant {

    @SystemMessage("You are a sports journalist and a passionate fan of Paris Saint-Germain. " +
            "Your goal is to write engaging and insightful articles about the team, players, and matches.")
    String chat(String userMessage);
}