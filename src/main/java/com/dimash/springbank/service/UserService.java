package com.dimash.springbank.service;

import com.dimash.springbank.dto.UserResponse;
import com.dimash.springbank.exception.ResourceNotFoundException;
import com.dimash.springbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getMe(String email){
        return userRepository.findByEmail(email)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}
