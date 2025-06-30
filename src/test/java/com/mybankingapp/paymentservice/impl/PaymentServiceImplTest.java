package com.mybankingapp.paymentservice.impl;

import com.mybankingapp.paymentservice.dto.*;
import com.mybankingapp.paymentservice.enums.PaymentFrequency;
import com.mybankingapp.paymentservice.enums.PaymentStatus;
import com.mybankingapp.paymentservice.mapper.PaymentMapper;
import com.mybankingapp.paymentservice.model.Payment;
import com.mybankingapp.paymentservice.repository.PaymentRepository;
import com.mybankingapp.paymentservice.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    private PaymentRepository repo;
    private PaymentMapper mapper;
    private PaymentServiceImpl service;

    private final UUID paymentId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repo = mock(PaymentRepository.class);
        mapper = mock(PaymentMapper.class);
        service = new PaymentServiceImpl(repo, mapper);
    }

    @Test
    void testCreatePayment() {
        CreatePaymentRequest req = new CreatePaymentRequest();
        req.setAccountId(accountId);
        req.setPayeeCode("TEST123");
        req.setDescription("Test payment");
        req.setAmount(BigDecimal.valueOf(100000));
        req.setCurrency("COP");
        req.setFrequency(PaymentFrequency.ONE_TIME);
        req.setFirstExecution(LocalDateTime.now().plusDays(1));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        when(mapper.toDto(any())).thenReturn(new PaymentResponse());

        PaymentResponse response = service.createPayment(req);

        verify(repo).save(captor.capture());
        Payment saved = captor.getValue();

        assertEquals("TEST123", saved.getPayeeCode());
        assertEquals(PaymentStatus.PENDING, saved.getStatus());
        assertNotNull(response);
    }

    @Test
    void testGetPaymentFound() {
        Payment payment = new Payment();
        payment.setId(paymentId);
        when(repo.findById(paymentId)).thenReturn(Optional.of(payment));
        when(mapper.toDto(payment)).thenReturn(new PaymentResponse());

        PaymentResponse result = service.getPayment(paymentId);
        assertNotNull(result);
    }

    @Test
    void testGetPaymentNotFound() {
        when(repo.findById(paymentId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getPayment(paymentId));
    }

    @Test
    void testListPaymentsByAccountId() {
        Payment p = new Payment();
        p.setId(paymentId);
        when(repo.findByAccountId(accountId)).thenReturn(List.of(p));
        when(mapper.toDto(any())).thenReturn(new PaymentResponse());

        List<PaymentResponse> result = service.listPayments(accountId, null);
        assertEquals(1, result.size());
    }

    @Test
    void testListPaymentsByStatus() {
        Payment p = new Payment();
        p.setId(paymentId);
        when(repo.findByStatus(PaymentStatus.PENDING)).thenReturn(List.of(p));
        when(mapper.toDto(any())).thenReturn(new PaymentResponse());

        List<PaymentResponse> result = service.listPayments(null, "PENDING");
        assertEquals(1, result.size());
    }

    @Test
    void testListAllPayments() {
        Payment p = new Payment();
        p.setId(paymentId);
        when(repo.findAll()).thenReturn(List.of(p));
        when(mapper.toDto(any())).thenReturn(new PaymentResponse());

        List<PaymentResponse> result = service.listPayments(null, null);
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateStatusCompleted() {
        Payment p = new Payment();
        p.setId(paymentId);
        p.setStatus(PaymentStatus.PROCESSING);

        PaymentStatusUpdate upd = new PaymentStatusUpdate();
        upd.setId(paymentId);
        upd.setStatus("COMPLETED");
        upd.setExternalReference("ABC123");

        when(repo.findById(paymentId)).thenReturn(Optional.of(p));

        service.updateStatus(upd);

        assertEquals(PaymentStatus.COMPLETED, p.getStatus());
        assertEquals("ABC123", p.getExternalReference());
        assertNotNull(p.getExecutedAt());
    }

    @Test
    void testCancelPaymentAllowed() {
        Payment p = new Payment();
        p.setId(paymentId);
        p.setStatus(PaymentStatus.PENDING);
        when(repo.findById(paymentId)).thenReturn(Optional.of(p));

        service.cancelPayment(paymentId);

        assertEquals(PaymentStatus.CANCELED, p.getStatus());
        assertNotNull(p.getUpdatedAt());
    }

    @Test
    void testCancelPaymentCompletedThrowsException() {
        Payment p = new Payment();
        p.setId(paymentId);
        p.setStatus(PaymentStatus.COMPLETED);
        when(repo.findById(paymentId)).thenReturn(Optional.of(p));

        assertThrows(IllegalStateException.class, () -> service.cancelPayment(paymentId));
    }
}
