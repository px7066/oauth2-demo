package com.github.px.custom.filter;

import com.github.px.custom.configurer.AuthClientConfigurer;
import com.github.px.custom.exception.AccessDenyException;
import com.github.px.custom.util.UrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "authRedirectFilter")
public class RedirectFilter implements Filter {

    private final static String scope = "openid";

    private final static String responseType = "code";

    private final static String action = "/oauth2/authorize";

    private final static String redirectActionFormat = "%s%s?response_type=%s&client_id=%s&scope=%s&redirect_uri=%s";

    @Autowired
    private AuthClientConfigurer clientConfigurer;



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =(HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (AccessDenyException e) {
            String redirectUrl = UrlBuilder.buildRedirectUrl(request);
            if(redirectUrl.contains("localhost")){
                redirectUrl = redirectUrl.replace("localhost", "127.0.0.1");
            }
            String redirectAction = String.format(redirectActionFormat, clientConfigurer.getAuthServer(), action, responseType, clientConfigurer.getClientId(), scope, redirectUrl);
            response.sendRedirect(redirectAction);
        }
    }


}
