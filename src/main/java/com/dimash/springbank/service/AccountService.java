package com.dimash.springbank.service;

import com.dimash.springbank.dto.AccountResponse;
import com.dimash.springbank.dto.DepositRequest;
import com.dimash.springbank.entity.Account;
import com.dimash.springbank.entity.User;
import com.dimash.springbank.exception.ResourceNotFoundException;
import com.dimash.springbank.repository.AccountRepository;
import com.dimash.springbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse createAccount(String email, String currency){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = new Account();
        account.setUser(user);
        account.setCurrency(currency);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(UUID.randomUUID().toString());

        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts(String email){
        return accountRepository.findByUserEmailOrderByIdAsc(email).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Transactional
    public AccountResponse deposit(DepositRequest depositRequest, String email){
        Account account = accountRepository.findByIdForUpdate(depositRequest.getAccountId())
                // 404 rather than 403 for foreign accounts, so account ids can't be enumerated
                .filter(a -> a.isOwnedBy(email))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(depositRequest.getAmount()));

        return AccountResponse.from(account);
    }
}
