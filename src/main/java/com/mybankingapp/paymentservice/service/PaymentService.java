package com.mybankingapp.paymentservice.service;

import com.mybankingapp.paymentservice.dto.CreatePaymentRequest;
import com.mybankingapp.paymentservice.dto.PaymentResponse;
import com.mybankingapp.paymentservice.dto.PaymentStatusUpdate;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse getPayment(UUID id);

    List<PaymentResponse> listPayments(UUID accountId, String status);

    void updateStatus(PaymentStatusUpdate update);

    void cancelPayment(UUID id);
}

