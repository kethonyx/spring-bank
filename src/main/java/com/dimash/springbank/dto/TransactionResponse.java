package com.dimash.springbank.dto;

import com.dimash.springbank.entity.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String currency,
        String senderAccountNumber,
        String receiverAccountNumber,
        Instant createdAt
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getSenderAccount().getCurrency(),
                transaction.getSenderAccount().getAccountNumber(),
                transaction.getReceiverAccount().getAccountNumber(),
                transaction.getCreatedAt());
    }
}
