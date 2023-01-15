package com.example.oauth2.security.domain;

import com.example.oauth2.member.domain.AuthProvider;

public interface UserProvider {
    AuthProvider getProvider();
}
