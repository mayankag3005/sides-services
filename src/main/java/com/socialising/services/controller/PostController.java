package com.socialising.services.controller;

import com.socialising.services.repository.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostRepository postRepository;

    // Inject the object of PostRepository using the Bean created in PostRepository Interface
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    public ResponseEntity getAllPosts() {

        System.out.println(this.postRepository.count());

        return ResponseEntity.ok(this.postRepository.findAll());
    }
}
