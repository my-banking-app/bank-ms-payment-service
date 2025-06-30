package com.mybankingapp.paymentservice.dto;

import com.mybankingapp.paymentservice.enums.PaymentFrequency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentResponse {
    private UUID id;
    private UUID accountId;
    private String payeeCode;
    private BigDecimal amount;
    private String currency;
    private PaymentFrequency frequency;
    private String status;
    private String externalReference;
    private LocalDateTime nextExecution;
    private LocalDateTime executedAt;
}