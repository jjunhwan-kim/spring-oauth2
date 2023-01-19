package com.example.oauth2.jwt.domain;

import com.example.oauth2.jwt.exception.InvalidTokenException;
import com.example.oauth2.jwt.service.RefreshTokenService;
import com.example.oauth2.member.domain.AuthProvider;
import com.example.oauth2.security.domain.UserProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;
    public static final String PROVIDER_CLAIM_NAME = "provider";
    public static final String AUTHORITIES_CLAIM_NAME = "authorities";
    private final RefreshTokenService refreshTokenService;
    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public Jwt createToken(Authentication authentication) {

        Object principal = authentication.getPrincipal();

        AuthProvider provider;

        if (principal instanceof UserProvider) {
            UserProvider userProvider = (UserProvider) principal;
            provider =  userProvider.getProvider();
        } else {
            throw new IllegalArgumentException();
        }

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(PROVIDER_CLAIM_NAME, provider.getRegistrationId())
                .claim(AUTHORITIES_CLAIM_NAME, authorities)
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

        return new Jwt(accessToken, refreshToken);
    }

    public void saveRefreshToken(Authentication authentication, String token) {
        String email = authentication.getName();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserProvider)) {
            throw new InvalidTokenException();
        }

        UserProvider provider = (UserProvider) principal;
        refreshTokenService.saveOrUpdate(provider.getProvider(), email, token);
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

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (claims.get(PROVIDER_CLAIM_NAME) == null || claims.get(AUTHORITIES_CLAIM_NAME) == null) {
            throw new IllegalArgumentException();
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_CLAIM_NAME).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails user = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }
}
