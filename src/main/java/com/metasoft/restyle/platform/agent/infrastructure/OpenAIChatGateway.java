package com.metasoft.restyle.platform.agent.infrastructure;

import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatMessage;
import com.metasoft.restyle.platform.agent.domain.model.aggregates.ChatResponse;
import com.metasoft.restyle.platform.agent.interfaces.ChatGateway;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputMessage;
import com.openai.models.responses.ResponseOutputText;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OpenAIChatGateway implements ChatGateway {

    private final OpenAIClient client;

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

            // Usamos el modelo gpt-5-nano
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model("gpt-5-nano")
                    .input(inputText)
                    .build();

            Response response = client.responses().create(params);

            // Extraer el texto correctamente
            StringBuilder outputText = new StringBuilder();
            
            List<ResponseOutputItem> outputs = response.output();
            if (outputs != null && !outputs.isEmpty()) {
                for (ResponseOutputItem output : outputs) {
                    // Manejar Optional correctamente
                    Optional<ResponseOutputMessage> messageOpt = output.message();
                    if (messageOpt.isPresent()) {
                        ResponseOutputMessage message = messageOpt.get();
                        List<ResponseOutputMessage.Content> contents = message.content();
                        
                        if (contents != null && !contents.isEmpty()) {
                            for (ResponseOutputMessage.Content content : contents) {
                                // Verificar si es texto
                                Optional<ResponseOutputText> textOpt = content.outputText();
                                if (textOpt.isPresent()) {
                                    ResponseOutputText text = textOpt.get();
                                    // text() devuelve String directamente, no Optional
                                    String textValue = text.text();
                                    if (textValue != null && !textValue.isEmpty()) {
                                        outputText.append(textValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String result = outputText.toString();
            if (result.isEmpty()) {
                result = "No se pudo obtener respuesta del modelo.";
            }

            return new ChatResponse(result);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al comunicarse con OpenAI API: " + e.getMessage(), e);
        }
    }
}