package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.dto.LoginRequest;
import com.formations.spring_products_api.dto.LoginResponse;
import com.formations.spring_products_api.dto.RegisterRequestDto;
import com.formations.spring_products_api.repository.IUserRepository;
import com.formations.spring_products_api.security.DomainUserDetails;
import com.formations.spring_products_api.security.jwt.JwtUserDetails;
import com.formations.spring_products_api.security.jwt.JwtUserDetailsClaimAdapter;
import com.formations.spring_products_api.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/registration")
	public ResponseEntity<Void> register(
		@Valid @RequestBody RegisterRequestDto request
	) {
		this.authService.register(request);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
		@Valid @RequestBody LoginRequest request
	) {
		return ResponseEntity.ok(this.authService.login(request));
	}
}
