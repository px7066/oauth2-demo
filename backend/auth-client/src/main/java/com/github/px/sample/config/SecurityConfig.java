package com.github.px.sample.config;

import com.github.px.sample.handler.CustomLoginUrlAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/webjars/**", "/static/**");
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.antMatchers("/", "/index.html", "/messages").authenticated()
                )
                .exceptionHandling().authenticationEntryPoint(new CustomLoginUrlAuthenticationEntryPoint("/oauth2/authorization/messaging-client-oidc"))
                .and()
                .oauth2Login(oauth2Login -> {
                    oauth2Login.loginPage("/oauth2/authorization/messaging-client-oidc");
                })
                .oauth2Client(withDefaults());
        AuthenticationManager authenticationManager = new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));
        http.addFilterBefore(new BearerTokenAuthenticationFilter(authenticationManager), FilterSecurityInterceptor.class);
        return http.build();
    }

}
