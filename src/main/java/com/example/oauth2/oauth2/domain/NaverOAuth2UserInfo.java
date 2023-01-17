package com.example.oauth2.oauth2.domain;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        // Naver 는 attributes 맵의 response 키의 값에 실제 attributes 맵이 할당되어 있음
        this.attributes = (Map<String, Object>) attributes.get("response");
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
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
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
        return (String) attributes.get("profile_image");
    }
}
