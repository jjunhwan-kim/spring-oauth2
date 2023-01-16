package com.example.oauth2.jwt.service;

import com.example.oauth2.member.domain.Member;
import com.example.oauth2.member.repository.MemberRepository;
import com.example.oauth2.security.domain.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + username));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().name());

        return new UserPrincipal(member.getEmail(),
                member.getPassword(),
                Collections.singletonList(grantedAuthority));
    }
}