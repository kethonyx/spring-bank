package com.dimash.springbank.service;

import com.dimash.springbank.dto.TransactionResponse;
import com.dimash.springbank.dto.TransferRequest;
import com.dimash.springbank.entity.Account;
import com.dimash.springbank.entity.Transaction;
import com.dimash.springbank.exception.InsufficientFundsException;
import com.dimash.springbank.exception.ResourceNotFoundException;
import com.dimash.springbank.repository.AccountRepository;
import com.dimash.springbank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void transfer(TransferRequest request){
        Account sender = accountRepository.findById(request
                .getSenderAccountId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sender not found")
                );

        Account receiver = accountRepository.findById(request
                .getReceiverAccountId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Receiver not found")
                );

        if (sender.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new InsufficientFundsException("Insufficient funds :(");

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
        transaction.setReceiverAccount(receiver);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

    }

    public List<TransactionResponse> getMyTransactions(String email){
        return transactionRepository.findBySenderAccountUserEmailOrReceiverAccountUserEmail(email, email).stream()
                .map(transaction -> new TransactionResponse(transaction.getId(), transaction.getAmount(), transaction.getSenderAccount().getAccountNumber(), transaction.getReceiverAccount().getAccountNumber(), transaction.getCreatedAt()))
                .toList();
    }

}
