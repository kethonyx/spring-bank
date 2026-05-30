package com.dimash.springbank.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotNull
    private Long senderAccountId;

    @NotNull
    private Long receiverAccountId;

    @DecimalMin("0.01")
    private BigDecimal amount;
}
