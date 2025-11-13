package com.metasoft.restyle.platform.agent.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ChatRequest", description = "Mensaje de entrada enviado por el usuario al asistente de Restyle.")
public record ChatRequestResource(
        @NotBlank(message = "El identificador de sesión no puede estar vacío.")
        @Schema(description = "Identificador único de la sesión del chat.", example = "session-12345")
        String sessionId,

        @NotBlank(message = "El nombre del usuario no puede estar vacío.")
        @Schema(description = "Nombre del usuario autenticado que interactúa con el asistente.", example = "Lucía Fernández")
        String userName,

        @NotBlank(message = "El rol del usuario no puede estar vacío.")
        @Schema(description = "Rol del usuario dentro de la plataforma.", example = "ROLE_REMODELER")
        String userRole,

        @NotBlank(message = "El mensaje no puede estar vacío.")
        @Schema(description = "Contenido del mensaje del usuario.", example = "Hola, ¿puedes ayudarme con ideas de remodelación?")
        String message
) {
}

