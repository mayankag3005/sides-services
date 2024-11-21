package com.socialising.services.controller;

import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.service.PostService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public Post addPost(@RequestBody Post post, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.postService.addPost(post, token);
    }

    @GetMapping("getAllPosts")
    @PreAuthorize("hasAuthority('admin:read')")
    public ArrayList<Post> getAllPosts() {
        return this.postService.getAllPosts();
    }

    @GetMapping("getPost/{id}")
    public Post getPostById(@PathVariable Long id) {
        return this.postService.getPostById(id);
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


    // Media

    // Add media to a post
    @PostMapping("/addMedia/{postId}/{mediaType}")
    public ResponseEntity<String> addMediaToPost(
            @PathVariable("postId") Long postId,
            @PathVariable("mediaType") String mediaType,
            @RequestBody MultipartFile file
            ) throws IOException {
        String updatedPost = postService.addMedia(postId, file, mediaType);
        return ResponseEntity.ok(updatedPost);
    }

    // Get all media (images and videos) of a post
    @GetMapping("/getMedia/{postId}")
    public ResponseEntity<Map<String, List<?>>> getAllMedia(@PathVariable Long postId) {
        Map<String, List<?>> media = postService.getAllMedia(postId);
        return ResponseEntity.ok(media);
    }

    // Edit existing media in a post
    @PutMapping("/editMedia/{postId}/{mediaType}/{oldId}")
    public ResponseEntity<String> editMediaInPost(
            @PathVariable("postId") Long postId,
            @PathVariable("mediaType") String mediaType,
            @PathVariable("oldId") String oldId,
            @RequestBody MultipartFile newFile) throws IOException {
        String updatedPost = postService.editMedia(postId, oldId, newFile, mediaType);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete Media
    @DeleteMapping("/removeMedia/{postId}/{mediaType}")
    public ResponseEntity<Integer> removeMediaFromPost(
            @PathVariable("postId") Long postId,
            @PathVariable("mediaType") String mediaType,
            @RequestParam String mediaId) {
        int updatedPost = postService.removeMedia(postId, mediaType, mediaId);
        return ResponseEntity.ok(updatedPost);
    }


}
