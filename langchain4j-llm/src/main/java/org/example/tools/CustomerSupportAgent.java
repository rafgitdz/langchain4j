package org.example.tools;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface CustomerSupportAgent {

    @SystemMessage("""
        You are a fruit and vegetable inventory manager.
        Today's date is {{current_date}}.
    """)
    String answer(String userMessage);
}
