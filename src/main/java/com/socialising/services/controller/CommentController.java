package com.socialising.services.controller;

import com.socialising.services.dto.CommentDTO;
import com.socialising.services.dto.CommentResponseDTO;
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
    public CommentResponseDTO addCommentOnPost(@PathVariable("postId") Long postId, @RequestBody CommentDTO commentDTO, @RequestHeader("Authorization") String token) {
        return commentService.addCommentOnPost(postId, commentDTO, token);
    }

    @GetMapping("getAllCommentsOnPost/{postId}")
    public ArrayList<CommentResponseDTO> getAllCommentsOnPost(@PathVariable("postId") Long postId) {
        return this.commentService.getAllCommentsOnPost(postId);
    }

    @DeleteMapping("deleteCommentOnPost/{postId}/{commentId}")
    public int deleteCommentOnPost(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @RequestHeader("Authorization") String token) {
        return this.commentService.deleteCommentOnPost(postId, commentId, token);
    }

    @PostMapping("likeComment/{commentId}")
    public int likeAComment(@PathVariable("commentId") Long commentId, @RequestHeader("Authorization") String token) {
        return this.commentService.likeAComment(commentId, token);
    }

    @GetMapping("getAllLikesOnComment/{commentId}")
    public String[] getAllLikesOnComment(@PathVariable Long commentId) {
        return this.commentService.getAllLikesOnComment(commentId);
    }

    @DeleteMapping("removeAlikeOnComment/{commentId}/{userId}")
    public int removeAlikeOnPost(@PathVariable("commentId") Long commentId, @RequestHeader("Authorization") String token) {
        return this.commentService.removeAlikeOnPost(commentId, token);
    }
}
