package com.example.oauth2.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {

    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao");

    private final String registrationId;
}
