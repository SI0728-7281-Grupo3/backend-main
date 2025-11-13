package com.metasoft.restyle.platform.agent.domain.model.aggregates;

public class ChatMessage {
    private final String role;   // "user" o "assistant"
    private final String content;

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() { return role; }
    public String getContent() { return content; }
}
