package com.github.px.sample.config;

import com.github.px.sample.handler.CustomLoginUrlAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/webjars/**", "/static/**");
    }

    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.antMatchers("/", "/index.html").authenticated()
                )
                .exceptionHandling().authenticationEntryPoint(new CustomLoginUrlAuthenticationEntryPoint("/oauth2/authorization/messaging-client-oidc"))
                .and()
                .oauth2Login(oauth2Login -> {
                    oauth2Login.loginPage("/oauth2/authorization/messaging-client-oidc");
                })
                .oauth2Client(withDefaults());
        return http.build();
    }

}
