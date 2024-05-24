package com.socialising.services.controller;

import com.socialising.services.model.Image;
import com.socialising.services.model.Post;
import com.socialising.services.repository.ImageRepository;
import com.socialising.services.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;

@RestController
@RequestMapping("/post/")
public class PostController {

    private final PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    // Inject the object of PostRepository using the Bean created in PostRepository Interface
    public PostController(PostRepository postRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
    }

    @PostMapping("/postAPost")
    public ResponseEntity addPost() {

        var post = new Post(1223L, 3256L, "this is a description", new Timestamp(2019-11- 5L) , "general", "current", "26-05-2024", "27-05-2024", "Bangalore", 'N');

        this.postRepository.save(post);

        System.out.println(this.postRepository.count());

        return ResponseEntity.ok(this.postRepository.findAll());
    }

    @GetMapping("/getAllPosts")
    public ResponseEntity getAllPosts() {

        System.out.println(this.postRepository.findAll());

        return ResponseEntity.ok(this.postRepository.findAll());
    }

    public void exampleImageUpload() throws Exception {
        var image = new Image(678L, Files.readAllBytes(Paths.get("backgate college.jpeg")) , "image/jpeg", "backgate college.jpeg");

        System.out.println("Image created not saved: " + image + ", count: " + this.imageRepository.count());
        this.imageRepository.save(image);

        System.out.println("Image saved: " + "count: " + this.imageRepository.count());
    }

    @GetMapping("getImage")
    public ResponseEntity getImage() throws Exception {
        System.out.println("image request received");
        exampleImageUpload();

        return ResponseEntity.ok(this.imageRepository.findAll());
    }
}
