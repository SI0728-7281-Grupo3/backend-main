package com.metasoft.restyle.platform.agent.infrastructure;

import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatMessage;
import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatResponse;
import com.metasoft.restyle.platform.agent.interfaces.ChatGateway;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OpenAIChatGateway implements ChatGateway {

    private final OpenAIClient client;

    // Se crea el cliente con la API key desde las variables de entorno o application.properties
    public OpenAIChatGateway(@Value("${openai.api.key}") String apiKey) {
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    @Override
    public ChatResponse sendToModel(List<ChatMessage> messages) {
        try {
            // Convertimos los mensajes a texto concatenado
            String inputText = messages.stream()
                    .map(m -> m.getRole() + ": " + m.getContent())
                    .collect(Collectors.joining("\n"));

            // Usamos el modelo más liviano (puedes usar "gpt-4o-mini" o "gpt-5-nano")
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model("gpt-5-nano")
                    .input(inputText)
                    .build();

            Response response = client.responses().create(params);

            String output = String.valueOf(response);
            return new ChatResponse(output);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al comunicarse con OpenAI API", e);
        }
    }
}
