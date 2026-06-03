package com.dimash.springbank.service;

import com.dimash.springbank.dto.LoginRequest;
import com.dimash.springbank.dto.LoginResponse;
import com.dimash.springbank.entity.User;
import com.dimash.springbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () ->  new RuntimeException("Invalid email or password")
        );

        boolean matches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if(!matches){
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.GenerateToken(user.getEmail());


        return new LoginResponse(token);
    }
}
