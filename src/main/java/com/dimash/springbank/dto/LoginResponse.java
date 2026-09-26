package com.dimash.springbank.dto;

public record LoginResponse(String token, String tokenType, long expiresIn) {
}
