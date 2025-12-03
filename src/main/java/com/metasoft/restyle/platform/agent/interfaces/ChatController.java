package com.metasoft.restyle.platform.agent.interfaces;

import com.metasoft.restyle.platform.agent.application.internal.commandservices.SendMessageHandler;
import com.metasoft.restyle.platform.agent.domain.model.commands.SendMessageCommand;
import com.metasoft.restyle.platform.agent.interfaces.rest.resources.ChatRequestResource;
import com.metasoft.restyle.platform.agent.interfaces.rest.resources.ChatResponseResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/chat", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
@Tag(name = "Agent Chat", description = "Endpoints para interactuar con el asistente virtual de Restyle.")
public class ChatController {

    private final SendMessageHandler sendMessageHandler;

    public ChatController(SendMessageHandler sendMessageHandler) {
        this.sendMessageHandler = sendMessageHandler;
    }

    @Operation(
            summary = "Enviar mensaje al asistente de Restyle",
            description = "Genera una respuesta del agente IA a partir del mensaje del usuario.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta generada correctamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChatResponseResource.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "No autorizado. Token inválido o ausente.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno al comunicarse con el modelo de IA.",
                    content = @Content)
    })
    @PostMapping(consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponseResource> sendMessage(@Valid @RequestBody ChatRequestResource request) {
        var response = sendMessageHandler.handle(new SendMessageCommand(
                request.sessionId(),
                request.userName(),
                request.userRole(),
                request.message()
        ));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ChatResponseResource.from(response));
    }
}
