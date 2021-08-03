package com.github.px.sample.config.configurer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "auth.server")
@Configuration
@Setter
@Getter
public class AuthServerConfigurer {
    private String loginFormUrl;

    private String failureUrl;
}
