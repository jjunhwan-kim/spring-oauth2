package com.example.oauth2.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

class TokenProviderTest {

    //@Test
    void createKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512); //or HS384 or HS512
        String secretString = Encoders.BASE64URL.encode(key.getEncoded());
        System.out.println(secretString);
    }

}