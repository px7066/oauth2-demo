package com.github.px.custom.filter;

import com.github.px.custom.exception.AccessDenyException;
import com.github.px.custom.matcher.AntPathRequestMatcher;
import com.github.px.custom.store.OidcIdToken;
import com.github.px.custom.store.TokenContext;
import com.github.px.custom.store.TokenResult;
import com.github.px.custom.util.JwtUtils;
import com.github.px.custom.util.StringUtils;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebFilter(filterName = "securityFilter")
public class SecurityFilter implements Filter {

    private List<AntPathRequestMatcher> matchers = new ArrayList<>();

    @PostConstruct
    public void init(){
        matchers.add(new AntPathRequestMatcher("/"));
        matchers.add(new AntPathRequestMatcher("/index"));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =(HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        for (AntPathRequestMatcher matcher : matchers) {
            if(matcher.matches(request)){
                TokenResult tokenResult = (TokenResult) request.getSession().getAttribute(TokenResult.TOKEN_RESULT_KEY);
                if(tokenResult == null){
                    Map<String, String> map = StringUtils.splitUrlParameters(request.getQueryString());
                    String code = map.get("code");
                    if(ObjectUtils.isEmpty(code)){
                        throw new AccessDenyException();
                    }
                }else {
                    TokenContext.setTokenResult(tokenResult);
                    try {
                        Map<String, Object> map = JwtUtils.parseToken(tokenResult.getId_token());
                        OidcIdToken idToken = new OidcIdToken(tokenResult.getId_token(), Instant.now(),Instant.now(), map);
                        request.getSession().setAttribute(OidcIdToken.TOKEN_RESULT_KEY, idToken);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }


}
