package com.neobank.authservice.util;

import com.neobank.authservice.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {

    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role",role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 Hours
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token){
        try{
            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
        }catch(SignatureException e){
            throw new InvalidTokenException("Invalid JWT Signature");
        }catch(JwtException e){
            throw new InvalidTokenException("Invalid JWT");
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid or expired JWT: " + e.getMessage());
        }
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new InvalidTokenException("JWT does not contain subject (email)");
        }
        return subject;
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
}
