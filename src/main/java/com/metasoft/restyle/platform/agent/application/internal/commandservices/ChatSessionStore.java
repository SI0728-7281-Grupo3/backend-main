package com.metasoft.restyle.platform.agent.application.internal.commandservices;

import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionStore {

    private static final int MAX_HISTORY = 20;

    private final Map<String, Deque<ChatMessage>> sessions = new ConcurrentHashMap<>();

    public List<ChatMessage> getHistory(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return List.of();
        }
        Deque<ChatMessage> history = sessions.get(sessionId);
        if (history == null || history.isEmpty()) {
            return List.of();
        }
        return List.copyOf(history);
    }

    public void appendMessage(String sessionId, ChatMessage message) {
        if (sessionId == null || sessionId.isBlank() || message == null) {
            return;
        }
        sessions.compute(sessionId, (id, current) -> {
            Deque<ChatMessage> history = current != null ? current : new ArrayDeque<>();
            history.addLast(message);
            while (history.size() > MAX_HISTORY) {
                history.removeFirst();
            }
            return history;
        });
    }

    public void clearSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        sessions.remove(sessionId);
    }
}

