package com.example.oauth2.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
