package com.dimash.springbank.service;

import com.dimash.springbank.dto.LoginRequest;
import com.dimash.springbank.dto.LoginResponse;
import com.dimash.springbank.dto.RegisterUserRequest;
import com.dimash.springbank.dto.UserResponse;
import com.dimash.springbank.entity.User;
import com.dimash.springbank.exception.DuplicateEmailException;
import com.dimash.springbank.exception.ResourceNotFoundException;
import com.dimash.springbank.exception.UnauthorizedException;
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
                () ->  new ResourceNotFoundException("User not found")
        );

        boolean matches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if(!matches){
            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateToken(user.getEmail());


        return new LoginResponse(token);
    }
    public UserResponse register(RegisterUserRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("User with this email already exists");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());


    }

}
