package com.github.px.sample.config;

import com.github.px.sample.handler.CustomLoginUrlAuthenticationEntryPoint;
import com.github.px.sample.handler.XhrAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/webjars/**");
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
//                .mvcMatcher("/messages")
//                .authorizeRequests()
//                .mvcMatchers("/messages").access("hasAuthority('SCOPE_message.read')")
//                .and()

                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )
                .exceptionHandling().authenticationEntryPoint(new CustomLoginUrlAuthenticationEntryPoint("/oauth2/authorization/messaging-client-oidc")).accessDeniedHandler(new XhrAccessDeniedHandler())
                .and()
                .oauth2Login(oauth2Login -> {
                    oauth2Login.loginPage("/oauth2/authorization/messaging-client-oidc");
                })
                .oauth2Client(withDefaults());
        return http.build();
    }

    private CorsConfiguration build(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        return corsConfiguration;
    }

    private UrlBasedCorsConfigurationSource source(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", build());
        return source;
    }
}
