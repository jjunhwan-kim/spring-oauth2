package com.example.oauth2.jwt.domain;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
