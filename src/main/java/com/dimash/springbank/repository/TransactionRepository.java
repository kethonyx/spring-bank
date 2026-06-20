package com.dimash.springbank.repository;

import com.dimash.springbank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderAccountUserEmailOrReceiverAccountUserEmail(String senderEmail, String receiverEmail);
}
