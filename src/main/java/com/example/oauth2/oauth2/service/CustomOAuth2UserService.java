package com.example.oauth2.oauth2.service;

import com.example.oauth2.member.domain.AuthProvider;
import com.example.oauth2.member.domain.Member;
import com.example.oauth2.member.repository.MemberRepository;
import com.example.oauth2.oauth2.exception.OAuth2AuthenticationProcessingException;
import com.example.oauth2.oauth2.user.OAuth2UserInfo;
import com.example.oauth2.oauth2.user.OAuth2UserInfoFactory;
import com.example.oauth2.security.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    /**
     * DefaultOAuth2UserService 구현체를 사용하여 oAuth2UserRequest 의 사용자 정보를 불러옵니다. (oAuth2User)
     *
     * @param oAuth2UserRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * OAuth2 사용자 정보를
     * @param userRequest
     * @param oAuth2User
     * @return
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId,
                oAuth2User.getAttributes());

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        AuthProvider provider = Arrays.stream(AuthProvider.values())
                .filter(authProvider -> authProvider.getRegistrationId().equals(registrationId))
                .findAny()
                .orElseThrow(() -> new OAuth2AuthenticationProcessingException("Registration ID not supported"));

        Optional<Member> memberOptional = memberRepository.findByEmailAndProvider(oAuth2UserInfo.getEmail(), provider);

        Member member;

        if (memberOptional.isPresent()) {
            member = memberOptional.get();

            if (!member.getProviderId().equals(oAuth2UserInfo.getId())) {
                throw new OAuth2AuthenticationProcessingException("Provider ID is invalid");
            }

            member = updateExistingUser(member, oAuth2UserInfo);
        } else {
            member = registerNewUser(provider, oAuth2UserInfo);
        }

        return OAuth2UserPrincipal.create(member, oAuth2UserInfo.getAttributes());
    }

    private Member registerNewUser(AuthProvider provider, OAuth2UserInfo oAuth2UserInfo) {

        Member member = Member.of(provider,
                oAuth2UserInfo.getId(),
                oAuth2UserInfo.getEmail(),
                oAuth2UserInfo.getName(),
                oAuth2UserInfo.getFirstName(),
                oAuth2UserInfo.getLastName(),
                oAuth2UserInfo.getNickname(),
                oAuth2UserInfo.getImage());

        return memberRepository.save(member);
    }

    private Member updateExistingUser(Member member, OAuth2UserInfo oAuth2UserInfo) {
        member.update(oAuth2UserInfo.getName(),
                oAuth2UserInfo.getFirstName(),
                oAuth2UserInfo.getLastName(),
                oAuth2UserInfo.getNickname(),
                oAuth2UserInfo.getImage());
        return memberRepository.save(member);
    }
}
