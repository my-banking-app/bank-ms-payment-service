package com.mybankingapp.paymentservice.model;

import com.mybankingapp.paymentservice.enums.PaymentStatus;
import com.mybankingapp.paymentservice.enums.PaymentFrequency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Payment {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false, length = 50)
    private String payeeCode;

    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String currency = "COP";

    @Enumerated(EnumType.STRING)
    private PaymentFrequency frequency;

    private LocalDateTime nextExecution;

    private String externalReference;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String failureReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime executedAt;
}

