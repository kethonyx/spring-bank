package com.dimash.springbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dimash.springbank.entity.User;
public interface UserRepository extends JpaRepository<User, Long> {

}
