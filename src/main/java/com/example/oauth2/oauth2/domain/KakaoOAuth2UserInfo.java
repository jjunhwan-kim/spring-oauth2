package com.example.oauth2.oauth2.domain;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final String email;
    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        // Kakao 는 attributes 맵의 kakao_account 키의 값에 실제 attributes 맵이 할당되어 있음
        String id = ((Long) attributes.get("id")).toString();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.email = (String) kakaoAccount.get("email");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
        this.attributes = kakaoProfile;
        this.attributes.put("id", id);
        this.attributes.put("email", this.email);
    }

    @Override
    public String getId() {
        return (String) this.getAttributes().get(this.getNameAttributeKey());
    }

    @Override
    public String getNameAttributeKey() {
        return "id";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("nickname");
    }

    @Override
    public String getImage() {
        return (String) attributes.get("profile_image_url");
    }
}
