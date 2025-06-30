package com.mybankingapp.paymentservice.config;

import com.mybankingapp.paymentservice.utils.SendUnauthorizedResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that checks for the presence and validity of an API key in the request headers.
 * Extends the OncePerRequestFilter to ensure that the filter is executed only once per request.
 */
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    /**
     * Instance of SendUnauthorizedResponse used to send unauthorized responses
     * when the API key is missing or invalid.
     */
    private SendUnauthorizedResponse sendUnauthorizedResponse = new SendUnauthorizedResponse();

    /**
     * The name of the header where the API key is expected.
     */
    private static final String API_KEY_HEADER = "x-api-key";

    /**
     * The expected API key value, injected from the application properties.
     */
    @Value("${api.key}")
    private String apikey;

    /**
     * Filters incoming requests to check for a valid API key.
     *
     * @param request the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @param filterChain the FilterChain for invoking the next filter or the resource
     * @throws ServletException if the request could not be handled
     * @throws IOException if an input or output error is detected when the servlet handles the request
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKeyHeader = request.getHeader(API_KEY_HEADER);


        log.info(">>> Validando API key para la solicitud {}", request.getRequestURI());

        if (apiKeyHeader == null || apikey == null) {
            sendUnauthorizedResponse.sendUnauthorizedResponse(response,
                    "Unauthorized: No API key found in request headers");

            return;
        }

        if (!apikey.equals(apiKeyHeader)) {
            log.warn("<<< API key inválida");
            sendUnauthorizedResponse.sendUnauthorizedResponse(response, "Unauthorized: Invalid API key");

            return;
        }

        log.info("<<< API key válida, continuando con la cadena de filtros");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return !path.startsWith("/api/v1/");
    }

}