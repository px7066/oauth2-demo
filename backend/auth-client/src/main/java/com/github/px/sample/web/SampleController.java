package com.github.px.sample.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    @GetMapping("/messages")
    public String[] getMessages() {
        return new String[] {"Message 1", "Message 2", "Message 3"};
    }
}
