package com.socialising.services.controller;

import com.socialising.services.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/details")
    public ResponseEntity getUserDetails() {
        System.out.println(userRepository.count());

        return ResponseEntity.ok(userRepository.findAll());
    }

}
