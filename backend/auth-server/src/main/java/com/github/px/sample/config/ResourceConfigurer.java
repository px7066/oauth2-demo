package com.github.px.sample.config;

import com.github.px.sample.config.configurer.AuthServerConfigurer;
import com.github.px.sample.custom.CustomAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
public class ResourceConfigurer{

    @Autowired
    private AuthServerConfigurer authServerConfigurer;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain resourceSecurityFilterChain(HttpSecurity http) throws Exception {
        // 认证失败处理
        AuthenticationFailureHandler authenticationFailureHandler = new CustomAuthenticationFailureHandler(authServerConfigurer.getFailureUrl());

        http.authorizeRequests()
                .mvcMatchers("/getUserInfo")
                .access("isAuthenticated()")
                .and()
                .oauth2ResourceServer()
                .jwt();
        http
                .formLogin()
                .loginPage(authServerConfigurer.getLoginFormUrl())
                .loginProcessingUrl("/login")
                .failureHandler(authenticationFailureHandler);
        return http.build();
    }

}
