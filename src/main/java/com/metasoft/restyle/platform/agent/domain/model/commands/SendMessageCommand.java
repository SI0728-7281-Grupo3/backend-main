package com.metasoft.restyle.platform.agent.domain.model.commands;

public class SendMessageCommand {
    private final String userMessage;

    public SendMessageCommand(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}

