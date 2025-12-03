package com.metasoft.restyle.platform.agent.domain.model.commands;

public class SendMessageCommand {
    private final String sessionId;
    private final String userName;
    private final String userRole;
    private final String userMessage;

    public SendMessageCommand(String sessionId, String userName, String userRole, String userMessage) {
        this.sessionId = sessionId;
        this.userName = userName;
        this.userRole = userRole;
        this.userMessage = userMessage;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUserMessage() {
        return userMessage;
    }
}

