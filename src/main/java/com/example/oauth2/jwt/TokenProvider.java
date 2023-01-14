package com.example.oauth2.jwt;

import com.example.oauth2.jwt.exception.InvalidTokenException;
import com.example.oauth2.member.domain.AuthProvider;
import com.example.oauth2.security.OAuth2UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;

@Component
public class TokenProvider {

    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;
    public static final String PROVIDER_CLAIM_NAME = "provider";

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public JwtToken createToken(Authentication authentication) {

        Object principal = authentication.getPrincipal();

        AuthProvider provider;

        if (principal instanceof OAuth2UserPrincipal) {
            OAuth2UserPrincipal oauth2UserPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
            provider = oauth2UserPrincipal.getProvider();
        } else {
            provider = AuthProvider.LOCAL;
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(PROVIDER_CLAIM_NAME, provider.getRegistrationId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(PROVIDER_CLAIM_NAME, provider.getRegistrationId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new JwtToken(accessToken, refreshToken);
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public AuthProvider getProvider(String token) {
        String providerRegistrationId = (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(PROVIDER_CLAIM_NAME);

        return Arrays.stream(AuthProvider.values())
                .filter(authProvider -> authProvider.getRegistrationId().equals(providerRegistrationId))
                .findAny()
                .orElseThrow(InvalidTokenException::new);
    }

    public void validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
