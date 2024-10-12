package org.example.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
interface Assistant {

    @SystemMessage("Tu es un journaliste sportive et fan du Paris Saint Germain !")
    String chat(String userMessage);
}