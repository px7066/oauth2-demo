package com.github.px.sample.web;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AuthorizationController {
    @GetMapping(value = "/authorize", params = "grant_type=authorization_code")
    public String authorizationCodeGrant(Model model,
                                         @RegisteredOAuth2AuthorizedClient("messaging-client-authorization-code")
                                                 OAuth2AuthorizedClient authorizedClient) {

//        String[] messages = this.webClient
//                .get()
//                .uri(this.messagesBaseUri)
//                .attributes(oauth2AuthorizedClient(authorizedClient))
//                .retrieve()
//                .bodyToMono(String[].class)
//                .block();
//        model.addAttribute("messages", messages);

        return "index";
    }
}
