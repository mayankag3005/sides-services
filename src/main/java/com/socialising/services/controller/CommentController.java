package com.socialising.services.controller;

import com.socialising.services.model.Comment;
import com.socialising.services.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/comment/")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("addCommentOnPost/{postId}")
    public Comment addCommentOnPost(@PathVariable("postId") Long postId, @RequestBody Comment newComment) {
        return this.commentService.addCommentOnPost(postId, newComment);
    }

    @GetMapping("getAllCommentsOnPost/{postId}")
    public ArrayList<Comment> getAllCommentsOnPost(@PathVariable("postId") Long postId) {
        return this.commentService.getAllCommentsOnPost(postId);
    }

    @DeleteMapping("deleteCommentOnPost/{postId}/{commentId}")
    public int deleteCommentOnPost(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        return this.commentService.deleteCommentOnPost(postId, commentId);
    }

    @PostMapping("likeComment/{commentId}/{userId}")
    public int likeAComment(@PathVariable("commentId") Long commentId, @PathVariable("userId") Long userId) {
        return this.commentService.likeAComment(commentId, userId);
    }

    @GetMapping("getAllLikesOnComment/{commentId}")
    public Long[] getAllLikesOnComment(@PathVariable Long commentId) {
        return this.commentService.getAllLikesOnComment(commentId);
    }

    @DeleteMapping("removeAlikeOnComment/{commentId}/{userId}")
    public int removeAlikeOnPost(@PathVariable("commentId") Long commentId, @PathVariable("userId") Long userId) {
        return this.commentService.removeAlikeOnPost(commentId, userId);
    }
}
