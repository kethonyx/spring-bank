package com.dimash.springbank.controller;

import com.dimash.springbank.dto.AccountResponse;
import com.dimash.springbank.dto.DepositRequest;
import com.dimash.springbank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public AccountResponse createAccount(@RequestParam String currency, Principal principal){

        return accountService.createAccount(principal.getName(), currency);
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
