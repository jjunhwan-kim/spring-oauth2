package com.example.oauth2.jwt;

import com.example.oauth2.jwt.domain.RefreshToken;
import com.example.oauth2.member.domain.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmailAndProvider(String email, AuthProvider provider);
}
