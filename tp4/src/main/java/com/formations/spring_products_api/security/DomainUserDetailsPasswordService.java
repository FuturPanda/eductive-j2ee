package com.formations.spring_products_api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

@Service
public class DomainUserDetailsPasswordService implements UserDetailsPasswordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainUserDetailsPasswordService.class);

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        if (user instanceof DomainUserDetails domainUser) {
            domainUser.getUser().setPassword(newPassword);
        } else {
            LOGGER.warn("Unable to updatePassword of {}", user);
        }
        return user;
    }
}
