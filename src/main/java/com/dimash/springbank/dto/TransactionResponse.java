package com.dimash.springbank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private LocalDateTime createdAt;


}
