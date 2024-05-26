package com.socialising.services.controller;

import com.socialising.services.model.Image;
import com.socialising.services.model.Post;
import com.socialising.services.repository.ImageRepository;
import com.socialising.services.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@RestController
@RequestMapping("/post/")
@Slf4j
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
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
        post.setCreatedTs();

        this.postRepository.save(post);

        log.info("Post added to db");

        return post;
    }

    @DeleteMapping("deletePost/{postid}")
    public void deletePost(@PathVariable Long postid) {
        if(this.postRepository.findById(postid).isPresent()) {
            this.postRepository.deleteById(postid);

            log.info("Post with Post ID: {} deleted from DB", postid);
        } else {
            log.info("No Post with Post Id: {} exists in DB", postid);
        }
    }

    @GetMapping("getAllPosts")
    public ArrayList<Post> getAllPosts() {

        log.info("Total number of posts: {}", this.postRepository.count());

        return (ArrayList<Post>) this.postRepository.findAll();
    }

    @GetMapping("getPost/{id}")
    public Post getPostById(@PathVariable Long id) {

        return this.postRepository.findById(id).isPresent() ? this.postRepository.findById(id).get() : null;
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
