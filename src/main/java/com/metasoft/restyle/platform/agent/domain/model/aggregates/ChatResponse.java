package com.metasoft.restyle.platform.agent.domain.model.aggregates;

public class ChatResponse {
    private final String content;

    public ChatResponse(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
}

