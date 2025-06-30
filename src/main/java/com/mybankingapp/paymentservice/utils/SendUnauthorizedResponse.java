package com.mybankingapp.paymentservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for sending unauthorized responses.
 */
@Slf4j
public class SendUnauthorizedResponse {

    /**
     * Sends an unauthorized response with a given message.
     *
     * @param response the HttpServletResponse to send the response to
     * @param message the message to include in the response body
     * @throws IOException if an input or output exception occurs
     */
    public void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        log.warn("Enviando respuesta no autorizada: {}", message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}