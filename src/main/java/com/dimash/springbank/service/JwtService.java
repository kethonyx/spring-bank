package com.dimash.springbank.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    private final static String SECRET =
            "this-is-my-super-secret-key-for-jwt-auth";

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(
                        SECRET.getBytes(
                                StandardCharsets.UTF_8
                        )
                );
    }

    public String extractEmail(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean isValid(String token){

        try{
            extractEmail(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String GenerateToken(String email){

        return Jwts.builder()
                .subject(email)
                .signWith(getKey())
                .compact();

    }
}
