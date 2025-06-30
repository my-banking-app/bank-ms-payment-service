package com.mybankingapp.paymentservice.dto;

import com.mybankingapp.paymentservice.enums.PaymentFrequency;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreatePaymentRequest {
    @NotNull private UUID accountId;
    @NotBlank private String payeeCode;
    private String description;
    @NotNull @Positive private BigDecimal amount;
    private String currency = "COP";
    private PaymentFrequency frequency = PaymentFrequency.ONE_TIME;
    private LocalDateTime firstExecution;
}