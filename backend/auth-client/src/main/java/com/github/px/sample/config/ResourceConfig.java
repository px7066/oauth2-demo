package com.github.px.sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceConfig {
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityFilterChain resourceFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/messages")
                .access("isAuthenticated()")
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();

    }
}
