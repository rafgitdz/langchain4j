package fr.rafdz.langchain4j.ai.agent;

import dev.langchain4j.service.SystemMessage;

public interface BanAgentSupport {

    @SystemMessage({
        "You are a customer support agent of a frances postal addresses.",
        "Today is {{current_date}}."
    })
    String chat(String userMessage);
}
