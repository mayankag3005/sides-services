package com.socialising.services.controller;

import com.socialising.services.dto.PostDTO;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.service.PostService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/post/")
@RequiredArgsConstructor
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    private boolean checkTokenValidity(String token) {
        if (!token.contains("Bearer ") || token.length() < 8) {
            log.info("It is an invalid token. Pass the valid token!");
            return false;
        }
        return true;
    }

    @PostMapping("addPost")
    public ResponseEntity<PostDTO> addPost(@RequestBody PostDTO postDTO, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        PostDTO createdPost = postService.addPost(postDTO, token);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("getAllPosts")
//    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<ArrayList<PostDTO>> getAllPosts() {
        return new ResponseEntity<>(this.postService.getAllPosts(), HttpStatus.OK) ;
    }

    @GetMapping("getPostsOfUser")
    public List<Post> getUserPosts(@RequestHeader("Authorization") String token) {
        return postService.getUserPosts(token);
    }

    @GetMapping("getPostsByUsername/{username}")
    public List<Post> getPostsByUsername(@PathVariable String username) {
        return postService.getPostsByUsername(username);
    }

    @GetMapping("getPost/{id}")
    public Post getPostById(@PathVariable Long id) {
        return this.postService.getPostById(id);
    }

    @PostMapping("updatePost/{id}")
    public ResponseEntity<?> updatePostDetails(@PathVariable Long id, @RequestHeader("Authorization") String token, @RequestBody PostDTO postDTO) {
        PostDTO updatedPost = postService.updatePost(id, token, postDTO);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("deletePost/{postId}")
    public int deletePostById(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.postService.deletePost(postId, token);
    }

    @PostMapping("interestedUserRequest/{postId}")
    public int postUserRequest(@PathVariable("postId") Long postId, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.postService.postUserRequest(postId, token);
    }

    @GetMapping("getInterestedUsers/{postId}")
    public List<String> getInterestedUsers(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.postService.getInterestedUsers(postId, token);
    }

    @PostMapping("acceptInterestedUser/{postId}/{username}")
    public int acceptInterestedUser(@PathVariable("postId") Long postId, @PathVariable("username") String username, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.postService.acceptInterestedUser(postId, username, token);
    }

    @PostMapping("rejectInterestedUser/{postId}/{username}")
    public int rejectInterestedUser(@PathVariable("postId") Long postId, @PathVariable("username") String username, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.postService.rejectInterestedUser(postId, username, token);
    }

    @GetMapping("getConfirmedUsers/{postId}")
    public List<String> getConfirmedUsers(@PathVariable Long postId) {
        return this.postService.getConfirmedUsers(postId);
    }

    @DeleteMapping("deleteConfirmedUser/{postId}/{username}")
    public int deleteConfirmedUser(@PathVariable("postId") Long postId, @PathVariable("username") String username, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.postService.deleteConfirmedUser(postId, username, token);
    }

    @PostMapping("likePost/{postId}")
    public int likeAPost(@PathVariable("postId") Long postId, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.postService.likeAPost(postId, token);
    }

    @GetMapping("getAllLikesOnPost/{postId}")
    public String[] getAllLikesOnPost(@PathVariable("postId") Long postId) {
        return this.postService.getAllLikesOnPost(postId);
    }

    @DeleteMapping("removeAlikeOnPost/{postId}")
    public int removeAlikeOnPost(@PathVariable("postId") Long postId, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.postService.removeAlikeOnPost(postId, token);
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
