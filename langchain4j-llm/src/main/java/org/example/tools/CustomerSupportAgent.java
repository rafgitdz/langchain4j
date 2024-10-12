package org.example.tools;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface CustomerSupportAgent {

    @SystemMessage("""
            Tu es un gestionnaire de stock de fruits et légumes
            Date du jour est {{current_date}}.
            """)
    String answer(String userMessage);
}
