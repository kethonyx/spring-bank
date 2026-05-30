package com.dimash.springbank.service;

import com.dimash.springbank.entity.Account;
import com.dimash.springbank.entity.User;
import com.dimash.springbank.repository.AccountRepository;
import com.dimash.springbank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository){
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createAccount(Long userId, String currency){
        User user = userRepository.findById(userId).orElseThrow();

        Account account = new Account();

        account.setUser(user);
        account.setCurrency(currency);
        account.setBalance(BigDecimal.ZERO);

        account.setAccountNumber(
                UUID.randomUUID().toString()
        );

        return accountRepository.save(account);



    }
}
