package com.dimash.springbank.repository;

import com.dimash.springbank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @EntityGraph(attributePaths = {"senderAccount", "receiverAccount"})
    @Query("""
            select t from Transaction t
            where t.senderAccount.user.email = :email
               or t.receiverAccount.user.email = :email
            """)
    Page<Transaction> findAllByUserEmail(String email, Pageable pageable);
}
