package com.metasoft.restyle.platform.agent.interfaces;

import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatMessage;
import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatResponse;


import java.util.List;

public interface ChatGateway {
    ChatResponse sendToModel(List<ChatMessage> messages);
}

