package com.mybankingapp.paymentservice.controller;

import com.mybankingapp.paymentservice.dto.*;
import com.mybankingapp.paymentservice.service.impl.PaymentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<PaymentResponse>> listPayments(
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(paymentService.listPayments(accountId, status));
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateStatus(
            @Valid @RequestBody PaymentStatusUpdate update) {
        paymentService.updateStatus(update);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable UUID id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.noContent().build();
    }
}
