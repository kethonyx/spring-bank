package com.dimash.springbank.controller;

import com.dimash.springbank.dto.UserResponse;
import com.dimash.springbank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe(Principal principal){
        return userService.getMe(principal.getName());
    }
}
