package com.dimash.springbank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositRequest {

    @NotNull
    private Long accountId;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal amount;
}
