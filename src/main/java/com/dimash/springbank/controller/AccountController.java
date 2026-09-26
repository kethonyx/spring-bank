package com.dimash.springbank.controller;

import com.dimash.springbank.dto.AccountResponse;
import com.dimash.springbank.dto.CreateAccountRequest;
import com.dimash.springbank.dto.DepositRequest;
import com.dimash.springbank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request, Principal principal){
        return accountService.createAccount(principal.getName(), request.getCurrency());
    }

    @GetMapping
    public List<AccountResponse> getMyAccounts(Principal principal){
        return accountService.getMyAccounts(principal.getName());
    }

    @PostMapping("/deposit")
    public AccountResponse deposit(@Valid @RequestBody DepositRequest depositRequest, Principal principal){
        return accountService.deposit(depositRequest, principal.getName());
    }

}
