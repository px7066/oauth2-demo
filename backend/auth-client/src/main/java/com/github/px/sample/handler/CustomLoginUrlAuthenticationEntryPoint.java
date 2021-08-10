package com.github.px.sample.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private String realmName;

    public static final AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher("/messages");

    public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(antPathRequestMatcher.matches(request)){
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            Map<String, String> parameters = new LinkedHashMap();
            if (this.realmName != null) {
                parameters.put("realm", this.realmName);
            }

            if (authException instanceof OAuth2AuthenticationException) {
                OAuth2Error error = ((OAuth2AuthenticationException)authException).getError();
                parameters.put("error", error.getErrorCode());
                if (StringUtils.hasText(error.getDescription())) {
                    parameters.put("error_description", error.getDescription());
                }

                if (StringUtils.hasText(error.getUri())) {
                    parameters.put("error_uri", error.getUri());
                }

                if (error instanceof BearerTokenError) {
                    BearerTokenError bearerTokenError = (BearerTokenError)error;
                    if (StringUtils.hasText(bearerTokenError.getScope())) {
                        parameters.put("scope", bearerTokenError.getScope());
                    }

                    status = ((BearerTokenError)error).getHttpStatus();
                }
            }

            String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
            response.addHeader("WWW-Authenticate", wwwAuthenticate);
            response.setStatus(status.value());
        }else{
            super.commence(request, response, authException);
        }


    }

    private static String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;

            for(Iterator var3 = parameters.entrySet().iterator(); var3.hasNext(); ++i) {
                Map.Entry<String, String> entry = (Map.Entry)var3.next();
                wwwAuthenticate.append((String)entry.getKey()).append("=\"").append((String)entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
            }
        }

        return wwwAuthenticate.toString();
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }
}
