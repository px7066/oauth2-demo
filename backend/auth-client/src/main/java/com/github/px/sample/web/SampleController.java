package com.github.px.sample.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/messages")
    public String[] getMessages() {
        return new String[] {"Message 1", "Message 2", "Message 3"};
    }

    @GetMapping("getToken")
    public String getToken(Authentication authentication){
        if(authentication != null){
            String username = authentication.getName();
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("messaging-client-oidc", username);
            if(authorizedClient != null){
                return authorizedClient.getAccessToken().getTokenValue();
            }
        }
        return null;
    }
}
