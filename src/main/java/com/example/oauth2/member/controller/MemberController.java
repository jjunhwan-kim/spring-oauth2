package com.example.oauth2.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    @GetMapping("/user")
    public String getUserInfo() {
        return "user";
    }

    @GetMapping("/admin")
    public String getAdminInfo() {
        return "admin";
    }
}
