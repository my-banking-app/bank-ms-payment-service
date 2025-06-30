package com.mybankingapp.paymentservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgumentException_deberiaRetornarResponseEntityConDatosEsperados() {
        String errorMessage = "Campo inválido";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<Object> response = handler.handleIllegalArgumentException(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(body.get("status")).isEqualTo(409);
        assertThat(body.get("error")).isEqualTo("Datos inválidos");
        assertThat(body.get("message")).isEqualTo(errorMessage);
        assertThat(body.get("timestamp")).isNotNull();
    }
}
