package com.github.px.sample.web;

import com.github.px.sample.persistence.User;
import com.github.px.sample.provisioning.JPAUserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private JPAUserManager jpaUserManager;

    @GetMapping("getUserInfo")
    public User getUserInfo(Authentication authentication){
        if(authentication instanceof JwtAuthenticationToken){
            JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
            return (User) jpaUserManager.loadUserByUsername(authenticationToken.getName());

        }else{
            if(authentication != null && !ObjectUtils.isEmpty(authentication.getPrincipal())){
                return (User) authentication.getPrincipal();
            }
        }
        return null;
    }
}
