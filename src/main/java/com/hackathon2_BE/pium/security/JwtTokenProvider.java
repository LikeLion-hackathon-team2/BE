package com.hackathon2_BE.pium.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessExpSeconds;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-exp-seconds:3600}") long accessExpSeconds,
            @Value("${app.jwt.issuer:flower-platform}") String issuer
    ) {
        this.key = buildHmacKey(secret);
        this.accessExpSeconds = accessExpSeconds;
        this.issuer = issuer;
    }

    /** username를 subject로, role을 클레임으로 넣어 토큰 생성 */
    public String createToken(String username, String role) {
        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + (accessExpSeconds * 1000L));

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(username)
                .claim("role", role)         // 예: "CONSUMER" / "SELLER"
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** "Bearer xxx" 형태 허용해서 Claims 반환 */
    public Claims parse(String token) throws SignatureException {
        String raw = stripBearer(token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(raw)
                .getBody();
    }

    /** 유효성만 확인하고 true/false 반환 */
    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** 토큰에서 subject(username) */
    public String getUsername(Claims claims) {
        return claims.getSubject();
    }

    /** 토큰에서 role */
    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    /** 남은 만료시간(초) – 생성 시점 기준 설정값 반환 */
    public long getExpiresInSeconds() {
        return accessExpSeconds;
    }

    // ================== 내부 유틸 ==================

    private static String stripBearer(String token) {
        if (token == null) return null;
        String t = token.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }

    private static Key buildHmacKey(String secret) {
        byte[] keyBytes;
        if (looksLikeBase64(secret)) {
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        // HS256 최소 256비트(32바이트) 권장
        if (keyBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 256 bits (32 bytes).");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static boolean looksLikeBase64(String s) {
        if (s == null) return false;
        String trimmed = s.trim();
        if (trimmed.length() % 4 != 0) return false;
        // BASE64에 사용 가능한 문자만 포함하는지 대략 검사
        return trimmed.matches("^[A-Za-z0-9+/=]+$");
    }
}
