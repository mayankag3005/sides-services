package com.socialising.services.controller;

import com.socialising.services.model.User;
import com.socialising.services.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/user/")
@Slf4j
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("details")
    public ArrayList<User> getAllUserDetails() {

        log.info("Total Number of users: {}", userRepository.count());

        return (ArrayList<User>) this.userRepository.findAll();
    }

    @GetMapping("details/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {

        return this.userRepository.findById(id);
    }

    @PostMapping("addUser")
    public User addUser(@RequestBody User user) {

        user.setUserId();

        this.userRepository.save(user);

        log.info("User added to the db");

        return user;
    }

}
