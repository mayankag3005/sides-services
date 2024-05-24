package com.socialising.services.controller;

import com.socialising.services.model.Image;
import com.socialising.services.model.Post;
import com.socialising.services.repository.ImageRepository;
import com.socialising.services.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/post/")
public class PostController {

    private final PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    // Inject the object of Repository using the Bean created in Repository Interface
    public PostController(PostRepository postRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
    }

    @PostMapping("addPost")
    public Post addPost(@RequestBody Post post) {

        post.setPostId();

        this.postRepository.save(post);

        System.out.println(this.postRepository.count());

        return post;
    }

    @GetMapping("getAllPosts")
    public ArrayList<Post> getAllPosts() {

        return (ArrayList<Post>) this.postRepository.findAll();
    }

    @GetMapping("getPost/{id}")
    public Optional<Post> getPostById(@PathVariable Long id) {

        Optional<Post> post = this.postRepository.findById(id);

        return post;
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
