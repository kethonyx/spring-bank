package com.dimash.springbank.controller;

import com.dimash.springbank.dto.RegisterUserRequest;
import com.dimash.springbank.dto.UserResponse;
import com.dimash.springbank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

}
