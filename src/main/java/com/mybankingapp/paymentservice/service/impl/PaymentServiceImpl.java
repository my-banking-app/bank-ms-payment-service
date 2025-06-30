package com.mybankingapp.paymentservice.service.impl;

import com.mybankingapp.paymentservice.dto.*;
import com.mybankingapp.paymentservice.enums.PaymentFrequency;
import com.mybankingapp.paymentservice.enums.PaymentStatus;
import com.mybankingapp.paymentservice.mapper.PaymentMapper;
import com.mybankingapp.paymentservice.model.Payment;
import com.mybankingapp.paymentservice.repository.PaymentRepository;
import com.mybankingapp.paymentservice.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repo;
    private final PaymentMapper mapper;

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest req) {

        Payment p = Payment.builder()
                .accountId(req.getAccountId())
                .payeeCode(req.getPayeeCode())
                .description(req.getDescription())
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .frequency(req.getFrequency())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .nextExecution(resolveFirstExecution(req))
                .build();

        repo.save(p);
        return mapper.toDto(p);
    }

    @Override
    public PaymentResponse getPayment(UUID id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Override
    public List<PaymentResponse> listPayments(UUID accountId, String status) {

        List<Payment> data;

        if (accountId != null) {
            data = repo.findByAccountId(accountId);
        } else if (status != null) {
            data = repo.findByStatus(PaymentStatus.valueOf(status));
        } else {
            data = repo.findAll();
        }
        return data.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(PaymentStatusUpdate upd) {

        Payment p = findOrThrow(upd.getId());

        PaymentStatus newStatus =
                PaymentStatus.valueOf(upd.getStatus().toUpperCase());

        p.setStatus(newStatus);
        p.setFailureReason(upd.getFailureReason());
        p.setExternalReference(upd.getExternalReference());
        p.setUpdatedAt(LocalDateTime.now());

        if (newStatus == PaymentStatus.COMPLETED) {
            p.setExecutedAt(LocalDateTime.now());
        }
    }

    @Override
    public void cancelPayment(UUID id) {
        Payment p = findOrThrow(id);

        if (p.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("No se puede cancelar un pago ya ejecutado");
        }
        p.setStatus(PaymentStatus.CANCELED);
        p.setUpdatedAt(LocalDateTime.now());
    }

    private Payment findOrThrow(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
    }

    private LocalDateTime resolveFirstExecution(CreatePaymentRequest req) {
        LocalDateTime firstExecution = req.getFirstExecution();
        if (req.getFrequency() == PaymentFrequency.ONE_TIME) {
            return (firstExecution != null) ? firstExecution : LocalDateTime.now();
        }
        return firstExecution;
    }
}
