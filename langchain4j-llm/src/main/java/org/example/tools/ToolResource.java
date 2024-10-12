package org.example.tools;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToolResource {
    private CustomerSupportAgent assistant;

    public ToolResource(CustomerSupportAgent assistant) {
        this.assistant = assistant;
    }

    @GetMapping("/tools")
    public String assistant(@RequestParam(value = "message") String message) {
        return this.assistant.answer(message);
    }
}
