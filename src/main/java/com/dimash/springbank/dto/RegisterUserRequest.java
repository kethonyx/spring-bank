package com.dimash.springbank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest {

    @NotBlank
    private String username;
    @Email
    @NotBlank
    private String email;
    @Size(min = 8)
    private String password;

    public String getUsername(){
        return username;
    }
}
