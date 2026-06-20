package com.dimash.springbank.repository;

import com.dimash.springbank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserEmail(String email);
}
