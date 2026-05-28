package com.smartcomplaint.smartcompaint.security;


import org.springframework.stereotype.Service;

import com.smartcomplaint.smartcompaint.entity.AppUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.time.Instant;


import java.nio.charset.StandardCharsets;


@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

 public String generateToken(AppUser user) {
    Instant now = Instant.now();

    return Jwts.builder()
            .setSubject(user.getEmail())
            .setClaims(Map.of(
                    "userId", user.getId().toString(),
                    "role", user.getRole().name()
            ))
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(properties.expirationMs())))
            .signWith(signingKey())
            .compact();
}

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractAllClaims(token).get("userId", String.class));
    }

    public boolean isTokenValid(String token, AppUser user) {
        return user.getEmail().equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
       return Jwts.parserBuilder()
            .setSigningKey(signingKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private SecretKey signingKey() {
        String secret = properties.secret();
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (RuntimeException ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
