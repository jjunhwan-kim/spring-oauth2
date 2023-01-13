package com.example.oauth2.oauth2.user;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return (String) this.getAttributes().get(this.getNameAttributeKey());
    }

    @Override
    public String getNameAttributeKey() {
        return "sub";
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
        return (String) attributes.get("given_name");
    }

    @Override
    public String getLastName() {
        return (String) attributes.get("family_name");
    }

    @Override
    public String getNickname() {
        return null;
    }

    @Override
    public String getImage() {
        return (String) attributes.get("picture");
    }
}
