package com.example.oauth2.member.repository;

import com.example.oauth2.member.domain.AuthProvider;
import com.example.oauth2.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndProvider(String email, AuthProvider provider);
}
