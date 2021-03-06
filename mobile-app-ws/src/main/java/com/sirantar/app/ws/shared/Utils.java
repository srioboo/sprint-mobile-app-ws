package com.sirantar.app.ws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.sirantar.app.ws.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class Utils {

	private static final Random RAMDOM = new SecureRandom();
	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	// private final int ITERATIONS = 10000;
	// private final int KEY_LENGTH = 256;

	public String generateUserId(int length) {
		return generateRandomString(length);
	}

	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {

		StringBuilder returnValue = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RAMDOM.nextInt(ALPHABET.length())));
		}

		return new String(returnValue);
	}

	public static boolean hasTokenExpired(String token) {

		boolean returnValue = true;

		try {
			Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token)
					.getBody();

			Date tokenExpirationDate = claims.getExpiration();
			Date todayDate = new Date();

			return tokenExpirationDate.before(todayDate);
		} catch (ExpiredJwtException e) {
			returnValue = true;
		}

		return returnValue;
	}

	public String generateEmailVerificationToken(String userId) {
		String token = Jwts.builder().setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();

		return token;
	}

	public String generatePasswordResetToken(String userId) {
		String token = Jwts.builder().setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();

		return token;
	}
}
