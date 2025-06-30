package com.mybankingapp.paymentservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(SecurityConfig.class)
public class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    public void testApiKeyFilterBean() {
        ApiKeyFilter apiKeyFilter = securityConfig.apiKeyFilter();
        assertNotNull(apiKeyFilter, "ApiKeyFilter should not be null");
    }

    @Test
    public void testPasswordEncoderBean() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertNotNull(passwordEncoder, "PasswordEncoder should not be null");
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder,
                "PasswordEncoder should be an instance of BCryptPasswordEncoder");
    }

    @Test
    public void testCorsConfigurationSource() {
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        assertNotNull(corsConfigurationSource, "CorsConfigurationSource should not be null");
    }
}
