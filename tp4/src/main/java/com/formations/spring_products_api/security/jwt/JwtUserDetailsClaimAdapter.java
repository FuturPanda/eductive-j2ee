package com.formations.spring_products_api.security.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtUserDetailsClaimAdapter {

	private static final String AUTHORITIES_KEY = "auth";
	private static final String AUTHORITIES_SEPARATOR = ",";

	private final JwtEncoder jwtEncoder;

	public JwtUserDetailsClaimAdapter(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	public String createToken(JwtUserDetails user) {
		String authorities = user
			.getAuthorities()
			.stream()
			.map(Object::toString)
			.reduce((a, b) -> a + AUTHORITIES_SEPARATOR + b)
			.orElse("");

		Instant now = Instant.now();
		Instant expiration = now.plus(30, ChronoUnit.DAYS);

		JwtClaimsSet claims = JwtClaimsSet.builder()
			.subject(user.getUsername())
			.claim(AUTHORITIES_KEY, authorities)
			.issuedAt(now)
			.expiresAt(expiration)
			.build();

		return jwtEncoder
			.encode(JwtEncoderParameters.from(claims))
			.getTokenValue();
	}

	public JwtUserDetails extractUserDetails(Jwt jwt) {
		String authoritiesString = jwt.getClaimAsString(AUTHORITIES_KEY);
		List<String> authorities = (authoritiesString == null ||
			authoritiesString.isEmpty())
			? Collections.emptyList()
			: Arrays.asList(authoritiesString.split(AUTHORITIES_SEPARATOR));

		return new JwtUserDetails(
			Long.parseLong(jwt.getSubject()),
			authorities
		);
	}
}
