package com.dimash.springbank.service;

import com.dimash.springbank.dto.LoginRequest;
import com.dimash.springbank.dto.LoginResponse;
import com.dimash.springbank.dto.RegisterUserRequest;
import com.dimash.springbank.dto.UserResponse;
import com.dimash.springbank.entity.User;
import com.dimash.springbank.exception.DuplicateEmailException;
import com.dimash.springbank.exception.UnauthorizedException;
import com.dimash.springbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request){
        // Same error for unknown email and wrong password, so attackers can't probe which emails are registered
        User user = userRepository.findByEmail(normalize(request.getEmail()))
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(token, "Bearer", jwtService.getExpiration().toSeconds());
    }

    @Transactional
    public UserResponse register(RegisterUserRequest request){
        String email = normalize(request.getEmail());

        if(userRepository.existsByEmail(email)){
            throw new DuplicateEmailException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return UserResponse.from(userRepository.save(user));
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

}
