package com.github.px.custom.filter;

import com.github.px.custom.store.IdTokenContext;
import com.github.px.custom.store.OidcIdToken;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter
public class IdTokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession httpSession = request.getSession();
        if(httpSession != null){
            OidcIdToken oidcIdToken = (OidcIdToken) httpSession.getAttribute(OidcIdToken.TOKEN_RESULT_KEY);
            if(oidcIdToken != null){
                IdTokenContext.setIdToken(oidcIdToken);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
