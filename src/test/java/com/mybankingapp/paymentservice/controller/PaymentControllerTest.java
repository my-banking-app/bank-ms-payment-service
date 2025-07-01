package com.mybankingapp.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybankingapp.paymentservice.dto.*;
import com.mybankingapp.paymentservice.enums.PaymentFrequency;
import com.mybankingapp.paymentservice.enums.PaymentStatus;
import com.mybankingapp.paymentservice.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentServiceImpl paymentService;

    private final UUID paymentId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();

    @Test
    void testCreatePayment() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setAccountId(accountId);
        request.setPayeeCode("PROVIDER01");
        request.setDescription("Pago mensual");
        request.setAmount(BigDecimal.valueOf(150000));
        request.setFrequency(PaymentFrequency.MONTHLY);

        PaymentResponse response = new PaymentResponse();
        response.setId(paymentId);
        response.setAccountId(accountId);
        response.setPayeeCode("PROVIDER01");
        response.setAmount(BigDecimal.valueOf(150000));
        response.setFrequency(PaymentFrequency.MONTHLY);
        response.setStatus("PENDING");

        Mockito.when(paymentService.createPayment(any(CreatePaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/payments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetPayment() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setId(paymentId);
        response.setAccountId(accountId);
        response.setPayeeCode("PROVIDER01");

        Mockito.when(paymentService.getPayment(eq(paymentId))).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId.toString()));
    }

    @Test
    void testListPayments() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setId(paymentId);
        response.setAccountId(accountId);
        response.setStatus("PENDING");

        Mockito.when(paymentService.listPayments(eq(accountId), eq("PENDING")))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/payments/list")
                        .param("accountId", accountId.toString())
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(paymentId.toString()));
    }

    @Test
    void testUpdateStatus() throws Exception {
        PaymentStatusUpdate update = new PaymentStatusUpdate();
        update.setId(paymentId);
        update.setStatus(PaymentStatus.COMPLETED.name());

        mockMvc.perform(patch("/api/v1/payments/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNoContent());

        Mockito.verify(paymentService).updateStatus(any(PaymentStatusUpdate.class));
    }

    @Test
    void testCancelPayment() throws Exception {
        mockMvc.perform(delete("/api/v1/payments/{id}", paymentId))
                .andExpect(status().isNoContent());

        Mockito.verify(paymentService).cancelPayment(eq(paymentId));
    }
}
