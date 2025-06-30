package com.mybankingapp.paymentservice.repository;

import com.mybankingapp.paymentservice.enums.PaymentStatus;
import com.mybankingapp.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByAccountId(UUID accountId);
    List<Payment> findByStatus(PaymentStatus status);
}
