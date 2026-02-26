package com.formations.spring_products_api.security.jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUserDetails implements UserDetails {

	private final Long id;
	private final Collection<? extends GrantedAuthority> authorities;

	public JwtUserDetails(Long id, List<String> authorities) {
		this.id = id;
		this.authorities = authorities
			.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toUnmodifiableList());
	}

	public Long getId() {
		return id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getUsername() {
		return id.toString();
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
}
