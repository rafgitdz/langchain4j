package org.example.basic;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LlmApiResource {

    private final ChatLanguageModel chatLanguageModel;

    public LlmApiResource(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/model")
    public String assistant(@RequestParam(value = "message", defaultValue = "j'aime") String message) {
        return this.chatLanguageModel.generate(message);
    }
}
