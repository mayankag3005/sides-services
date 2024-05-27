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
import java.util.Arrays;
import java.util.List;

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

    @PostMapping("interestedUserRequest/{postid}/{userid}")
    public int postUserRequest(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {
        if(this.postRepository.findById(postid).isPresent()) {

            Post post = this.postRepository.findById(postid).get();

            long[] interestedUsers = post.getInterestedUsers() ;

            log.info("Interested Users before: {}", interestedUsers);

            if(Arrays.asList(interestedUsers).contains(userid)) {
                log.info("User {} exists in Interested Users list for Post {}", userid, postid);

                return interestedUsers.length;
            } else {
                if(interestedUsers == null) {

                    interestedUsers = new long[] {userid};

                } else {
                    int intLen = interestedUsers.length;
                    long[] newInterestedUsers = new long[intLen + 1];

                    for (int i = 0; i < intLen ; i++)
                        newInterestedUsers[i] = interestedUsers[i];

                    newInterestedUsers[intLen] = userid;
                    interestedUsers = newInterestedUsers;
                }

                post.setInterestedUsers(interestedUsers);

                this.postRepository.save(post);

                log.info("Interested Users for Post: {} are {}", postid, interestedUsers);

                return interestedUsers.length;
            }
        }

        log.info("No such post exist with Post Id: {}", postid);
        return -1;
    }

    @GetMapping("getInterestedUsers/{postid}")
    public long[] getInterestedUsers(@PathVariable Long postid) {
        if(this.postRepository.findById(postid).isPresent()) {

            long[] interestedUsers = this.postRepository.findById(postid).get().getInterestedUsers();

            if(interestedUsers == null) {
                log.info("No Interested Users for Post {}", postid);
            } else {
                log.info("Interested Users for Post {} are {}", postid, interestedUsers.length);
            }

            return interestedUsers;
        }

        log.info("No Post with Post ID: {}", postid);
        return null;
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
