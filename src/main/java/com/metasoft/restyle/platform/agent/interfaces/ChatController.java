package com.metasoft.restyle.platform.agent.interfaces;

import com.metasoft.restyle.platform.agent.domain.model.commands.SendMessageCommand;
import com.metasoft.restyle.platform.agent.application.internal.commandservices.SendMessageHandler;
import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final SendMessageHandler sendMessageHandler;

    public ChatController(SendMessageHandler sendMessageHandler) {
        this.sendMessageHandler = sendMessageHandler;
    }

    @PostMapping
    public Map<String, String> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        ChatResponse response = sendMessageHandler.handle(new SendMessageCommand(userMessage));
        return Map.of("response", response.getContent());
    }
}
