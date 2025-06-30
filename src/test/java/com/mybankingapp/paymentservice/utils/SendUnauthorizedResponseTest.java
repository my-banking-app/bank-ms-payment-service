package com.mybankingapp.paymentservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SendUnauthorizedResponseTest {

    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    private SendUnauthorizedResponse sendUnauthorizedResponse;

    @BeforeEach
    void setUp() {
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        sendUnauthorizedResponse = new SendUnauthorizedResponse();
    }

    @Test
    void sendUnauthorizedResponse_deberiaEnviarJsonConMensajeYStatus401() throws Exception {
        String mensaje = "Token inválido";

        when(response.getWriter()).thenReturn(printWriter);

        sendUnauthorizedResponse.sendUnauthorizedResponse(response, mensaje);

        // Validar que se haya seteado el status y content type
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        // Validar que el JSON devuelto contiene el mensaje
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseJson = objectMapper.readValue(stringWriter.toString(), new ObjectMapper().getTypeFactory().constructMapType(Map.class, String.class, String.class));

        assertThat(responseJson)
                .containsEntry("message", mensaje);
    }
}
