package com.github.px.custom.web;

import com.github.px.custom.store.IdTokenContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    @GetMapping("/messages")
    public String[] getMessages() {
        return new String[] {"Message 1", "Message 2", "Message 3"};
    }

    @GetMapping("   getUsername")
    public String getUsername(){
        return IdTokenContext.getIdToken().getUsername();
    }
}
