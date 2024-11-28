package org.example.aiservice;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AssistantController {

    private final Assistant assistant;

    public AssistantController(Assistant assistant) {
        this.assistant = assistant;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/assistant")
    public String assistant(
            @RequestParam(value = "message", defaultValue = "what's your favourite team?") String message
    ) {
        return this.assistant.chat(message);
    }
}
