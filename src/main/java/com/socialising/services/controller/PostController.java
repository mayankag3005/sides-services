package com.socialising.services.controller;

import com.socialising.services.model.Post;
import com.socialising.services.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/post/")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    // Inject the object of Repository using the Bean created in Repository Interface
    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("addPost")
    public Post addPost(@RequestBody Post post) {
        return this.postService.addPost(post);
    }

    @GetMapping("getAllPosts")
    public ArrayList<Post> getAllPosts() {
        return this.postService.getAllPosts();
    }

    @GetMapping("getPost/{id}")
    public Post getPostById(@PathVariable Long id) {
        return this.postService.getPostById(id);
    }

    @DeleteMapping("deletePost/{postid}")
    public void deletePostById(@PathVariable Long postid) {
        this.postService.deletePost(postid);
    }

    @PostMapping("interestedUserRequest/{postid}/{userid}")
    public int postUserRequest(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {
        return this.postService.postUserRequest(postid, userid);
    }

    @GetMapping("getInterestedUsers/{postid}")
    public Long[] getInterestedUsers(@PathVariable Long postid) {
        return this.postService.getInterestedUsers(postid);
    }

    @PostMapping("acceptInterestedUser/{postid}/{userid}")
    public int acceptInterestedUser(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {
        return this.postService.acceptInterestedUser(postid, userid);
    }

    @PostMapping("rejectInterestedUser/{postid}/{userid}")
    public int rejectInterestedUser(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {
        return this.postService.rejectInterestedUser(postid, userid);
    }

    @GetMapping("getConfirmedUsers/{postid}")
    public Long[] getConfirmedUsers(@PathVariable Long postid) {
        return this.postService.getConfirmedUsers(postid);
    }

    @DeleteMapping("deleteConfirmedUser/{postid}/{userid}")
    public Long[] deleteConfirmedUser(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {
        return this.postService.deleteConfirmedUser(postid, userid);
    }

    @PostMapping("likePost/{postId}/{userId}")
    public int likeAPost(@PathVariable("postId") Long postId, @PathVariable("userId") Long userId) {
        return this.postService.likeAPost(postId, userId);
    }

    @GetMapping("getAllLikesOnPost/{postId}")
    public Long[] getAllLikesOnPost(@PathVariable("postId") Long postId) {
        return this.postService.getAllLikesOnPost(postId);
    }

    @DeleteMapping("removeAlikeOnPost/{postId}/{userId}")
    public int removeAlikeOnPost(@PathVariable("postId") Long postId, @PathVariable("userId") Long userId) {
        return this.postService.removeAlikeOnPost(postId, userId);
    }

    @PostMapping("addHashtags/{postId}")
    public String[] addHashtagsToPost(@PathVariable Long postId, @RequestBody String[] newHashtags) {
        return this.postService.addHashtags(postId, newHashtags);
    }

    @GetMapping("getHashtags/{postId}")
    public String[] getHashtagsToPost(@PathVariable Long postId) {
        return this.postService.getHashtagsOfPost(postId);
    }

    @PutMapping("updateHashtags/{postId}")
    public String[] updateHashtagsToPost(@PathVariable Long postId, @RequestBody String[] newHashtags) {
        return this.postService.updateHashtags(postId, newHashtags);
    }

    @DeleteMapping("deleteHashtags/{postId}")
    public int deleteHashtagToPost(@PathVariable Long postId, @RequestBody String hashtag) {
        return this.postService.deleteHashtagsOfPost(postId, hashtag);
    }
}
