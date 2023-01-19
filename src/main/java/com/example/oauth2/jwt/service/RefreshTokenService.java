package com.example.oauth2.jwt.service;

import com.example.oauth2.jwt.RefreshTokenRepository;
import com.example.oauth2.jwt.domain.RefreshToken;
import com.example.oauth2.member.domain.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveOrUpdate(AuthProvider provider, String email, String token) {
        refreshTokenRepository.findByEmailAndProvider(email, provider).ifPresentOrElse(
                refreshToken -> refreshToken.changeToken(token), () -> {
                    RefreshToken refreshToken = RefreshToken.of(provider, email, token);
                    refreshTokenRepository.save(refreshToken);
                }
        );
    }
}