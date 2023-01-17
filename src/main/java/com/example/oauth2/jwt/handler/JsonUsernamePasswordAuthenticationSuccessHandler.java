package com.example.oauth2.jwt.handler;

import com.example.oauth2.jwt.Jwt;
import com.example.oauth2.jwt.JwtProvider;
import com.example.oauth2.jwt.domain.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class JsonUsernamePasswordAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {

        Jwt token = jwtProvider.createToken(authentication);

        LoginResponse loginResponse = LoginResponse.of(token.getAccessToken(), token.getRefreshToken());

        try {
            String tokenResponse = objectMapper.writeValueAsString(loginResponse);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(tokenResponse);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
