package com.dimash.springbank.service;

import com.dimash.springbank.dto.TransferRequest;
import com.dimash.springbank.entity.Account;
import com.dimash.springbank.entity.Transaction;
import com.dimash.springbank.repository.AccountRepository;
import com.dimash.springbank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void transfer(TransferRequest request){
        Account sender = accountRepository.findById(request
                .getSenderAccountId())
                .orElseThrow();

        Account receiver = accountRepository.findById(request
                .getReceiverAccountId())
                .orElseThrow();

        if (sender.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new RuntimeException("Insufficient funds :(");

        }

        sender.setBalance(
                sender.getBalance()
                        .subtract(request.getAmount())
        );

        receiver.setBalance(
                receiver.getBalance()
                        .add(request.getAmount())
        );

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setSenderAccount(sender);
        transaction.setRecieverAccount(receiver);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

    }

}
