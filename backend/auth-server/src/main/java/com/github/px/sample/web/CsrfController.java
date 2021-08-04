package com.github.px.sample.web;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CsrfController {
    @GetMapping("/getCsrfToken")
    public String getCsrfToken(HttpServletRequest request){
        if(request.getAttribute(CsrfToken.class.getName()) == null){
            return null;
        }
        return ((CsrfToken) request.getAttribute(CsrfToken.class.getName())).getToken();
    }
}
