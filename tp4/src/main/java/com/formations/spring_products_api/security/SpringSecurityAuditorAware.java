package com.formations.spring_products_api.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    public static final String SYSTEM_ACCOUNT = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        String login = SecurityUtils.getCurrentUserLogin();
        return Optional.of(login != null ? login : SYSTEM_ACCOUNT);
    }
}
