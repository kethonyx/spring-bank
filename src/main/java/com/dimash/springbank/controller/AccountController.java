package com.dimash.springbank.controller;

import com.dimash.springbank.entity.Account;
import com.dimash.springbank.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping
    public Account createAccount(@RequestParam Long userId, @RequestParam String currency){

        return accountService.createAccount(userId, currency);
    }


}
