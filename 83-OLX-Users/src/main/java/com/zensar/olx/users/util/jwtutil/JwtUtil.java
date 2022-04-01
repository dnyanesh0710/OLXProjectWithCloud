package com.zensar.olx.users.util.jwtutil;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JwtUtil {

	private final String SECRET_KEY="zensar@1234#";
	
	public String generateToken(String username) {
		
		String token=JWT.create()
			.withClaim("username", username)
			.withExpiresAt(new Date(System.currentTimeMillis()+1800000))
			.sign(Algorithm.HMAC512(SECRET_KEY));
		return token;
	}
	
	public String validateToken(String token) {
		return JWT.require(Algorithm.HMAC512(SECRET_KEY))
			.build()
			.verify(token)
			.getPayload();
	}
}
