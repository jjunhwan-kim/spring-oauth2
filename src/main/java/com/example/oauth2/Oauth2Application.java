package com.example.oauth2;

import com.example.oauth2.member.domain.Member;
import com.example.oauth2.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@SpringBootApplication
public class Oauth2Application {

	private final MemberRepository memberRepository;
	private final PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(Oauth2Application.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		if (memberRepository.findByEmail("admin@admin.com").isEmpty()) {
			memberRepository.save(Member.createDefaultAdmin(encoder));
		}
	}
}
