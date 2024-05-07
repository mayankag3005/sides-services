package com.socialising.services.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class AuthController {

    @CrossOrigin(origins = "*")
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
