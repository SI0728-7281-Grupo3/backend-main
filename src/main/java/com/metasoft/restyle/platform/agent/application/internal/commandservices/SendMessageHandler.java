package com.metasoft.restyle.platform.agent.application.internal.commandservices;

import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatMessage;
import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatResponse;
import com.metasoft.restyle.platform.agent.domain.model.commands.SendMessageCommand;
import com.metasoft.restyle.platform.agent.interfaces.ChatGateway;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SendMessageHandler {

    private final ChatGateway chatGateway;

    public SendMessageHandler(ChatGateway chatGateway) {
        this.chatGateway = chatGateway;
    }

    public ChatResponse handle(SendMessageCommand command) {
        List<ChatMessage> messages = List.of(
                new ChatMessage("system", "Eres un asistente para un proyecto universitario."),
                new ChatMessage("user", command.getUserMessage())
        );

        return chatGateway.sendToModel(messages);
    }
}
