package com.dimash.springbank.repository;

import com.dimash.springbank.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserEmailOrderByIdAsc(String email);

    // SELECT ... FOR UPDATE: blocks concurrent writers until the current transaction ends,
    // so two parallel transfers can't both read the same balance and overdraw the account.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a join fetch a.user where a.id = :id")
    Optional<Account> findByIdForUpdate(Long id);
}
