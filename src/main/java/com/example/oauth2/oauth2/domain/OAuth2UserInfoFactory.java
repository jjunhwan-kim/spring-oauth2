package com.example.oauth2.oauth2.domain;

import com.example.oauth2.member.domain.AuthProvider;
import com.example.oauth2.oauth2.exception.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                    Map<String, Object> attributes) {
        if (AuthProvider.GOOGLE.getRegistrationId().equals(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (AuthProvider.NAVER.getRegistrationId().equals(registrationId)) {
            return new NaverOAuth2UserInfo(attributes);
        } else if (AuthProvider.KAKAO.getRegistrationId().equals(registrationId)) {
            return new KakaoOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
