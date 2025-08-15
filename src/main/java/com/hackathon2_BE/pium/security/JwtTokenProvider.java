package com.hackathon2_BE.pium.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.Claims;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final String BASE64_SECRET =
            "ZmFrZV9zZWNyZXRfZmFrZV9zZWNyZXRfZmFrZV9zZWNyZXRfZmFrZV9zZWNyZXQ=";

    private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(BASE64_SECRET));
    private final long accessTokenMillis = 60 * 60 * 1000L; // 1h

    public String createToken(String username, String role) {
        long now = System.currentTimeMillis();
        Date expiry = new Date(now + accessTokenMillis);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) throws SignatureException {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    public long getExpiresInSeconds() { return accessTokenMillis / 1000; }
}
