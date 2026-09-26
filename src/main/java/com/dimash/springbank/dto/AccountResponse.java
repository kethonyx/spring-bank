package com.dimash.springbank.dto;

import com.dimash.springbank.entity.Account;

import java.math.BigDecimal;

public record AccountResponse(Long id, String accountNumber, BigDecimal balance, String currency) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance(), account.getCurrency());
    }
}
