package com.example.oauth2.member.controller;

import com.example.oauth2.member.service.MemberService;
import com.example.oauth2.member.service.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }


    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpRequest request) {
        memberService.signUp(request);
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "admin";
    }
}
