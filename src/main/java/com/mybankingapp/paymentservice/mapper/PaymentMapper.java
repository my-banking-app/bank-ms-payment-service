package com.mybankingapp.paymentservice.mapper;

import com.mybankingapp.paymentservice.dto.PaymentResponse;
import com.mybankingapp.paymentservice.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toDto(Payment p) {
        PaymentResponse dto = new PaymentResponse();
        dto.setId(p.getId());
        dto.setAccountId(p.getAccountId());
        dto.setPayeeCode(p.getPayeeCode());
        dto.setAmount(p.getAmount());
        dto.setCurrency(p.getCurrency());
        dto.setFrequency(p.getFrequency());
        dto.setStatus(p.getStatus().name());
        dto.setExternalReference(p.getExternalReference());
        dto.setNextExecution(p.getNextExecution());
        dto.setExecutedAt(p.getExecutedAt());
        return dto;
    }
}
