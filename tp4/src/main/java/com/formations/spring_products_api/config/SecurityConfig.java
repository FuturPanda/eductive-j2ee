package com.formations.spring_products_api.config;

import com.formations.spring_products_api.security.DomainUserDetailsPasswordService;
import com.formations.spring_products_api.security.DomainUserDetailsService;
import com.formations.spring_products_api.security.jwt.JwtUserDetailsClaimAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtDecoder jwtDecoder;
	private final JwtUserDetailsClaimAdapter jwtUserDetailsClaimAdapter;

	public SecurityConfig(
		JwtDecoder jwtDecoder,
		JwtUserDetailsClaimAdapter jwtUserDetailsClaimAdapter
	) {
		this.jwtDecoder = jwtDecoder;
		this.jwtUserDetailsClaimAdapter = jwtUserDetailsClaimAdapter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
		DomainUserDetailsService userDetailsService,
		DomainUserDetailsPasswordService userDetailsPasswordService,
		PasswordEncoder passwordEncoder
	) {
		DaoAuthenticationProvider authenticationProvider =
			new DaoAuthenticationProvider(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		authenticationProvider.setUserDetailsPasswordService(
			userDetailsPasswordService
		);
		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth ->
				auth
					.requestMatchers(
						"/swagger-ui/**",
						"/swagger-ui.html",
						"/v3/api-docs/**"
					)
					.permitAll()
					.requestMatchers("/api/auth/**")
					.permitAll()
					.anyRequest()
					.authenticated()
			)
			.oauth2ResourceServer(oauth2 ->
				oauth2.jwt(jwt ->
					jwt
						.decoder(jwtDecoder)
						.jwtAuthenticationConverter(source -> {
							var jwtUserDetails =
								jwtUserDetailsClaimAdapter.extractUserDetails(
									source
								);
							var token = new JwtAuthenticationToken(
								source,
								jwtUserDetails.getAuthorities()
							);
							token.setDetails(jwtUserDetails);
							return token;
						})
				)
			);
		return http.build();
	}
}
