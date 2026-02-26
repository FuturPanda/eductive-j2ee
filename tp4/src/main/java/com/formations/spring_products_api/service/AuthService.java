package com.formations.spring_products_api.service;

import com.formations.spring_products_api.dto.LoginRequest;
import com.formations.spring_products_api.dto.LoginResponse;
import com.formations.spring_products_api.dto.RegisterRequestDto;
import com.formations.spring_products_api.model.User;
import com.formations.spring_products_api.repository.IUserRepository;
import com.formations.spring_products_api.security.DomainUserDetails;
import com.formations.spring_products_api.security.jwt.JwtUserDetails;
import com.formations.spring_products_api.security.jwt.JwtUserDetailsClaimAdapter;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtUserDetailsClaimAdapter jwtUserDetailsClaimAdapter;
	private final IUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(
		AuthenticationManager authenticationManager,
		JwtUserDetailsClaimAdapter jwtUserDetailsClaimAdapter,
		IUserRepository userRepository,
		PasswordEncoder passwordEncoder
	) {
		this.authenticationManager = authenticationManager;
		this.jwtUserDetailsClaimAdapter = jwtUserDetailsClaimAdapter;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void register(RegisterRequestDto registerDto) {
		if (userRepository.existsByEmail(registerDto.email())) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"User already exists"
			);
		}

		User user = new User(
			registerDto.email(),
			passwordEncoder.encode(registerDto.password())
		);
		userRepository.save(user);
	}

	public LoginResponse login(LoginRequest request) {
		var authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				request.email(),
				request.password()
			)
		);

		var domainUserDetails =
			(DomainUserDetails) authentication.getPrincipal();
		var jwtUserDetails = new JwtUserDetails(
			domainUserDetails.getId(),
			domainUserDetails
				.getAuthorities()
				.stream()
				.map(Object::toString)
				.toList()
		);

		String token = jwtUserDetailsClaimAdapter.createToken(jwtUserDetails);
		return new LoginResponse(token);
	}
}
