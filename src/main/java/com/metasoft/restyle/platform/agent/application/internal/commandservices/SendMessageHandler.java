package com.metasoft.restyle.platform.agent.application.internal.commandservices;

import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatMessage;
import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatResponse;
import com.metasoft.restyle.platform.agent.domain.model.commands.SendMessageCommand;
import com.metasoft.restyle.platform.agent.interfaces.ChatGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SendMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageHandler.class);
    private final ChatGateway chatGateway;
    private final ChatSessionStore chatSessionStore;

    public SendMessageHandler(ChatGateway chatGateway, ChatSessionStore chatSessionStore) {
        this.chatGateway = chatGateway;
        this.chatSessionStore = chatSessionStore;
    }

    public ChatResponse handle(SendMessageCommand command) {
        LOGGER.info("Received chat request - SessionId: {}, UserName: {}, UserRole: {}, Message: {}", 
                command.getSessionId(), command.getUserName(), command.getUserRole(), command.getUserMessage());
        
        ChatMessage userMessage = new ChatMessage("user", command.getUserMessage());

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", buildSystemPrompt(command)));
        messages.addAll(chatSessionStore.getHistory(command.getSessionId()));
        messages.add(userMessage);

        ChatResponse response = chatGateway.sendToModel(messages);

        chatSessionStore.appendMessage(command.getSessionId(), userMessage);
        chatSessionStore.appendMessage(command.getSessionId(), new ChatMessage("assistant", response.getContent()));

        return response;
    }

    private String buildSystemPrompt(SendMessageCommand command) {
        String basePrompt = "Eres ReStyle Assistant, un experto en la plataforma ReStyle, una solución digital que conecta a clientes que desean remodelar su hogar con empresas y profesionales del rubro. Tu objetivo es guiar a los usuarios en la contratación de servicios, el seguimiento del avance de los proyectos, la gestión de tareas y la compra de materiales y mobiliario. Considera que el modelo de negocio se basa en planes premium para profesionales, mientras que los clientes finales usan la plataforma sin costo. Ofrece respuestas claras, accionables y siempre en español, manteniendo un tono cercano y profesional. Si falta información, haz preguntas concretas para obtenerla.";

        String userName = command.getUserName() != null && !command.getUserName().isBlank()
                ? command.getUserName()
                : "el usuario";

        String roleDescription = describeRole(command.getUserRole());

        return basePrompt + " Estás conversando con " + userName + ", " + roleDescription + ". Ajusta tus recomendaciones a su perfil, utiliza su nombre cuando sea apropiado y prioriza la información relevante para su rol.";
    }

    private String describeRole(String userRole) {
        if (userRole == null || userRole.isBlank()) {
            return "un usuario de la plataforma";
        }
        return switch (userRole) {
            case "ROLE_ADMIN" -> "administrador/a de ReStyle enfocado en supervisar operaciones y soporte";
            case "ROLE_REMODELER" -> "profesional remodelador que usa las herramientas premium para gestionar proyectos y captar clientes";
            case "ROLE_CONTRACTOR" -> "cliente que busca remodelar su hogar y contrata servicios dentro de la plataforma";
            default -> "un usuario de la plataforma";
        };
    }
}
