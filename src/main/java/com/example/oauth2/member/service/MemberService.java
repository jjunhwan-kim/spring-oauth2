package com.example.oauth2.member.service;

import com.example.oauth2.member.domain.Member;
import com.example.oauth2.member.service.request.SignUpRequest;
import com.example.oauth2.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public void signUp(SignUpRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String nickname = request.getNickname();
        String image = request.getImage();
        Member member = Member.of(email,
                password,
                firstName,
                lastName,
                nickname,
                image,
                passwordEncoder);

        memberRepository.save(member);
    }
}
