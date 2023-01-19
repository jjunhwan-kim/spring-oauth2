package com.example.oauth2.security.config;

import com.example.oauth2.jwt.filter.JsonUsernamePasswordAuthenticationFilter;
import com.example.oauth2.jwt.filter.JwtAuthorizationFilter;
import com.example.oauth2.jwt.handler.JsonUsernamePasswordAuthenticationFailureHandler;
import com.example.oauth2.jwt.handler.JsonUsernamePasswordAuthenticationSuccessHandler;
import com.example.oauth2.jwt.handler.JwtAccessDeniedHandler;
import com.example.oauth2.jwt.handler.JwtAuthenticationEntryPoint;
import com.example.oauth2.oauth2.config.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.oauth2.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.example.oauth2.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.example.oauth2.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JsonUsernamePasswordAuthenticationSuccessHandler jsonUsernamePasswordAuthenticationSuccessHandler;
    private final JsonUsernamePasswordAuthenticationFailureHandler jsonUsernamePasswordAuthenticationFailureHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JsonUsernamePasswordAuthenticationFilter(
                        "/api/auth/signin",
                        authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)),
                        jsonUsernamePasswordAuthenticationSuccessHandler,
                        jsonUsernamePasswordAuthenticationFailureHandler), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter, JsonUsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/error",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                .permitAll()
                .antMatchers("/api/auth/**")
                .permitAll()
                .antMatchers("/api/members/user").hasRole("USER")
                .antMatchers("/api/members/admin").hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        return http.build();
    }
}