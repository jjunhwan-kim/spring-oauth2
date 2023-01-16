package com.example.oauth2.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Jwt {
    private final String accessToken;
    private final String refreshToken;
}
