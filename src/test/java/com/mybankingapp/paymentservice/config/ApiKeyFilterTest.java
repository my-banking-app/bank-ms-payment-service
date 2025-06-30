package com.mybankingapp.paymentservice.config;

import com.mybankingapp.paymentservice.utils.SendUnauthorizedResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.Mockito.*;

class ApiKeyFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SendUnauthorizedResponse sendUnauthorizedResponse;

    @InjectMocks
    private ApiKeyFilter apiKeyFilter;

    @Value("${api.key}")
    private String apiKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(apiKeyFilter, "apikey", "expected-api-key");
    }

    @Test
    void shouldAllowRequestWhenApiKeyIsValid() throws ServletException, IOException {
        when(request.getHeader("x-api-key")).thenReturn("expected-api-key");

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(sendUnauthorizedResponse, never()).sendUnauthorizedResponse(any(), anyString());
    }

    @Test
    void shouldRejectRequestWhenApiKeyIsMissing() throws ServletException, IOException {
        when(request.getHeader("x-api-key")).thenReturn(null);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(sendUnauthorizedResponse, times(1)).sendUnauthorizedResponse(response, "Unauthorized: No API key found in request headers");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldRejectRequestWhenApiKeyIsInvalid() throws ServletException, IOException {
        when(request.getHeader("x-api-key")).thenReturn("invalid-api-key");

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(sendUnauthorizedResponse, times(1)).sendUnauthorizedResponse(response, "Unauthorized: Invalid API key");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldNotFilter_deberiaRetornarTrueParaRutaQueNoEmpiezaConApi() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/health");

        ApiKeyFilter filter = new ApiKeyFilter() {
            @Override
            public boolean shouldNotFilter(HttpServletRequest request) {
                return super.shouldNotFilter(request);
            }
        };

        boolean resultado = filter.shouldNotFilter(request);

        assert(resultado);
    }

    @Test
    void shouldNotFilter_deberiaRetornarFalseParaRutaQueEmpiezaConApi() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/payments");

        ApiKeyFilter filter = new ApiKeyFilter() {
            @Override
            public boolean shouldNotFilter(HttpServletRequest request) {
                return super.shouldNotFilter(request);
            }
        };

        boolean resultado = filter.shouldNotFilter(request);

        assert(!resultado);
    }

}