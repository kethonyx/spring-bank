package com.dimash.springbank.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(
                        secret.getBytes(
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

    public String generateToken(String email){

        return Jwts.builder()
                .subject(email)
                .signWith(getKey())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .compact();

    }
}
