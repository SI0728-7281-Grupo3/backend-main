package com.metasoft.restyle.platform.shared.interfaces.rest;

import com.openai.errors.RateLimitException;
import com.openai.errors.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler.
 * <p>
 * This class handles exceptions globally across all REST controllers.
 * It provides consistent error responses for validation errors and other exceptions.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation exceptions (MethodArgumentNotValidException).
     * This ensures that validation errors return 400 Bad Request instead of 401 Unauthorized.
     *
     * @param ex the validation exception
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");
        response.put("message", "Los datos de la solicitud no son válidos");
        response.put("errors", errors);

        LOGGER.warn("Validation failed: {}", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles OpenAI API exceptions (UnauthorizedException).
     * This ensures that OpenAI API errors return 500 Internal Server Error instead of 401 Unauthorized.
     *
     * @param ex the OpenAI unauthorized exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleOpenAIUnauthorizedException(UnauthorizedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "OpenAI API Error");
        response.put("message", "Error al comunicarse con el servicio de IA. Verifique la configuración de la API key de OpenAI.");

        LOGGER.error("OpenAI API unauthorized error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles OpenAI API rate limit exceptions (RateLimitException).
     * This occurs when the API quota has been exceeded.
     *
     * @param ex the OpenAI rate limit exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Map<String, Object>> handleOpenAIRateLimitException(RateLimitException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        response.put("error", "OpenAI API Quota Exceeded");
        response.put("message", "Se ha excedido la cuota de la API de OpenAI. Por favor, verifique su plan y detalles de facturación en https://platform.openai.com/account/billing");

        LOGGER.error("OpenAI API rate limit error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }

    /**
     * Handles RuntimeException exceptions that may occur during request processing.
     * This prevents Spring Security from losing authentication context and returning 401.
     *
     * @param ex the runtime exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        
        // Si es un error relacionado con OpenAI, devolvemos un mensaje específico
        if (ex.getMessage() != null && ex.getMessage().contains("OpenAI API")) {
            // Verificar si es un error de cuota
            if (ex.getMessage().contains("429") || ex.getMessage().contains("quota") || ex.getMessage().contains("exceeded")) {
                response.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
                response.put("error", "OpenAI API Quota Exceeded");
                response.put("message", "Se ha excedido la cuota de la API de OpenAI. Por favor, verifique su plan y detalles de facturación.");
            } else {
                response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.put("error", "OpenAI API Error");
                response.put("message", "Error al comunicarse con el servicio de IA. Verifique la configuración de la API key de OpenAI.");
            }
            LOGGER.error("OpenAI API error: {}", ex.getMessage(), ex);
        } else {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("error", "Internal Server Error");
            response.put("message", "Ocurrió un error interno al procesar la solicitud.");
            LOGGER.error("Runtime exception: {}", ex.getMessage(), ex);
        }
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

