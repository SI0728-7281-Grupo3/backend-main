package com.metasoft.restyle.platform.agent.interfaces.rest.resources;

import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ChatResponse", description = "Respuesta generada por el asistente de Restyle.")
public record ChatResponseResource(
        @Schema(description = "Contenido de la respuesta del asistente.", example = "Hola Lucia, dime en qué parte de tu proyecto necesitas ayuda.")
        String response
) {

    public static ChatResponseResource from(ChatResponse chatResponse) {
        return new ChatResponseResource(chatResponse.getContent());
    }
}

