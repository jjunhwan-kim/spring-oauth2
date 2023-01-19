package com.example.oauth2.jwt.domain;

import com.example.oauth2.member.domain.AuthProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private AuthProvider provider;

	private String email;

	private String token;

	private RefreshToken(AuthProvider provider, String email, String token) {
		this.provider = provider;
		this.email = email;
		this.token = token;
	}

	public static RefreshToken of(AuthProvider provider, String email, String token) {
		return new RefreshToken(provider, email, token);
	}

	public void changeToken(String token) {
		this.token = token;
	}
}