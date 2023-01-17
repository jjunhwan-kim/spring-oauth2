package com.example.oauth2.member.controller;

import com.example.oauth2.member.service.MemberService;
import com.example.oauth2.member.service.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpRequest request) {
        memberService.signUp(request);
    }
}
