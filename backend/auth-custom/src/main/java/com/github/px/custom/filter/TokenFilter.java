package com.github.px.custom.filter;

import com.github.px.custom.configurer.AuthClientConfigurer;
import com.github.px.custom.store.TokenResult;
import com.github.px.custom.util.StringUtils;
import com.github.px.custom.util.UrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@WebFilter
public class TokenFilter implements Filter {

    private static final String tokenActionFormat = "%s/oauth2/token";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthClientConfigurer authClientConfigurer;

    private static final BASE64Encoder base64Encoder = new BASE64Encoder();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =(HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Map<String, String> map = StringUtils.splitUrlParameters(request.getQueryString());
        String code = map.get("code");
        if(!ObjectUtils.isEmpty(code)){
            String url = String.format(tokenActionFormat, authClientConfigurer.getAuthServer());
            String redirectUrl = UrlBuilder.buildFullRequestUrl(request.getScheme(), request.getServerName(), request.getServerPort(), request.getRequestURI(), null);
            URI uri = null;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.set("redirect_uri", redirectUrl);
            parameters.set("grant_type", "authorization_code");
            parameters.set("code", code);

            String authorizationSource = authClientConfigurer.getClientId() + ":" + authClientConfigurer.getSecret();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json;charset=UTF-8");
            headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            headers.add("Authorization", "Basic " + base64Encoder.encode(authorizationSource.getBytes(StandardCharsets.UTF_8)));

            RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity(parameters, headers, HttpMethod.POST, uri);
            ResponseEntity<TokenResult> tokenResultResponseEntity = restTemplate.exchange(requestEntity, TokenResult.class);
            if(tokenResultResponseEntity.getStatusCode() == HttpStatus.OK){
                request.getSession().setAttribute(TokenResult.TOKEN_RESULT_KEY, tokenResultResponseEntity.getBody());
            }
            response.sendRedirect(redirectUrl);
            return;
        }
        filterChain.doFilter(request, response);
    }

}
