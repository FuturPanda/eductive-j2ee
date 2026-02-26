package com.formations.spring_products_api.security;

import com.formations.spring_products_api.repository.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DomainUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    public DomainUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findOneByEmailAndDeletedFalse(username)
            .map(DomainUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
    }
}
