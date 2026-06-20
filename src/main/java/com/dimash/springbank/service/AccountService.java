package com.dimash.springbank.service;

import com.dimash.springbank.dto.AccountResponse;
import com.dimash.springbank.dto.DepositRequest;
import com.dimash.springbank.entity.Account;
import com.dimash.springbank.entity.User;
import com.dimash.springbank.exception.ForbiddenException;
import com.dimash.springbank.exception.ResourceNotFoundException;
import com.dimash.springbank.repository.AccountRepository;
import com.dimash.springbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponse createAccount(String email, String currency){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        Account account = new Account();

        account.setUser(user);
        account.setCurrency(currency);
        account.setBalance(BigDecimal.ZERO);

        account.setAccountNumber(
                UUID.randomUUID().toString()
        );

        accountRepository.save(account);

        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance(), account.getCurrency());



    }

    public List<AccountResponse> getMyAccounts(String email){
        List<AccountResponse> accounts = accountRepository.findByUserEmail(email).stream()
                .map(account -> new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance(), account.getCurrency()))
                .toList();

        return accounts;



    }

    public AccountResponse deposit(DepositRequest depositRequest, String email){
        Account account = accountRepository.findById(depositRequest.getAccountId()).orElseThrow(
                () -> new ResourceNotFoundException("Account not found")
        );

        if(account.getUser().getEmail().equals(email)) {

            account.setBalance(account.getBalance().add(depositRequest.getAmount()));

            accountRepository.save(account);

            return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance(), account.getCurrency());
        }else
            throw new ForbiddenException("Account does not belong to current user!");
    }
}
