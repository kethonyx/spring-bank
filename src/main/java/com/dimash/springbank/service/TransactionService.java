package com.dimash.springbank.service;

import com.dimash.springbank.dto.TransactionResponse;
import com.dimash.springbank.dto.TransferRequest;
import com.dimash.springbank.entity.Account;
import com.dimash.springbank.entity.Transaction;
import com.dimash.springbank.exception.InsufficientFundsException;
import com.dimash.springbank.exception.InvalidTransferException;
import com.dimash.springbank.exception.ResourceNotFoundException;
import com.dimash.springbank.repository.AccountRepository;
import com.dimash.springbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse transfer(TransferRequest request, String email){
        Long senderId = request.getSenderAccountId();
        Long receiverId = request.getReceiverAccountId();
        BigDecimal amount = request.getAmount();

        if (senderId.equals(receiverId)) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }

        // Always lock rows in ascending id order: two opposite transfers (A->B and B->A)
        // running at the same time would otherwise deadlock.
        Account first = lock(Math.min(senderId, receiverId));
        Account second = lock(Math.max(senderId, receiverId));
        Account sender = first.getId().equals(senderId) ? first : second;
        Account receiver = sender == first ? second : first;

        if (!sender.isOwnedBy(email)) {
            throw new ResourceNotFoundException("Account not found");
        }
        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            throw new InvalidTransferException("Currency mismatch: " + sender.getCurrency() + " -> " + receiver.getCurrency());
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setSenderAccount(sender);
        transaction.setReceiverAccount(receiver);

        return TransactionResponse.from(transactionRepository.saveAndFlush(transaction));
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getMyTransactions(String email, Pageable pageable){
        return transactionRepository.findAllByUserEmail(email, pageable)
                .map(TransactionResponse::from);
    }

    private Account lock(Long id) {
        return accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

}
