package com.example.oauth2.oauth2.user;

import java.util.Map;

public interface OAuth2UserInfo {
    String getId();
    String getNameAttributeKey();
    Map<String, Object> getAttributes();
    String getEmail();
    String getName();
    String getFirstName();
    String getLastName();
    String getNickname();
    String getImage();
}
