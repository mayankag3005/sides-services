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

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @PostMapping("addCommentOnPost/{postId}")
    public Comment addCommentOnPost(@PathVariable("postId") Long postId, @RequestBody Comment newComment) {
//        if(!checkPostExistInDB(postId)) {
//            return null;
//        }
//
//        // Get the user id of the comment
//        Long userId = newComment.getUserId();
//
//        if(!checkUserExistInDB(userId)) {
//            return null;
//        }
//
//        // Set a unique comment id
//        newComment.setCommentId();
//
//        // Add New comment to Comment DB
//        this.commentRepository.save(newComment);
//
//        // Add new Comment to Comments list of the post
//        Post post = this.postRepository.findById(postId).get();
//        Long[] comments = post.getComments();
//        comments = ArrayUtils.add(comments, newComment.getCommentId());
//        post.setComments(comments);
//        this.postRepository.save(post);
//
//        log.info("New Comment [{}] has been added to Comments DB", newComment.getDescription());
//        return newComment;
        return this.commentService.addCommentOnPost(postId, newComment);
    }

    @GetMapping("getAllCommentsOnPost/{postId}")
    public ArrayList<Comment> getAllCommentsOnPost(@PathVariable("postId") Long postId) {
//        if(!checkPostExistInDB(postId)) {
//            return null;
//        }
//
//        Post post = this.postRepository.findById(postId).get();
//        Long[] commentIds = post.getComments();
//
//        if(ArrayUtils.isEmpty(commentIds)) {
//            log.info("No Comments has been added to the Post {}", postId);
//            return null;
//        }
//        log.info("No. of Comments added to the Post {} are: {}", postId, commentIds.length);
//
//        // Get the comments from comment DB by looping over comment IDs
//        ArrayList<Comment> comments = new ArrayList<>();
//        for(Long commentId : commentIds) {
//            comments.add(this.commentRepository.findById(commentId).get());
//        }
//        return comments;
        return this.commentService.getAllCommentsOnPost(postId);
    }

    @DeleteMapping("deleteCommentOnPost/{postId}/{commentId}")
    public int deleteCommentOnPost(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
//        if(!checkPostExistInDB(postId)) {
//            return -1;
//        }
//
//        Post post = this.postRepository.findById(postId).get();
//        Long[] commentIds = post.getComments();
//
//        // Remove Comment to Comments list of the post
//        if(ArrayUtils.isEmpty(commentIds)) {
//            log.info("No comments added to the Post {}", postId);
//            return -1;
//        }
//
//        // Check if the comment exists in the post's comment Id list
//        if(!ArrayUtils.contains(commentIds, commentId)) {
//            log.info("Comment {} is not added to the Post {}. Please check!!", commentId, postId);
//            return -1;
//        }
//
//        // remove comment from comment DB
//        this.commentRepository.deleteById(commentId);
//        log.info("Comment {} deleted from DB", commentId);
//
//        // Remove comment from the Post comment's list
//        commentIds = ArrayUtils.removeElement(commentIds, commentId);
//        post.setComments(commentIds);
//        this.postRepository.save(post);
//
//        log.info("Comment [{}] has been deleted from the Comments list of the Post {}", commentId, postId);
//        return 1;
        return this.commentService.deleteCommentOnPost(postId, commentId);
    }

    @PostMapping("likeComment/{commentId}/{userId}")
    public int likeAComment(@PathVariable("commentId") Long commentId, @PathVariable("userId") Long userId) {
//        if(!checkCommentExistInDB(commentId)) {
//            return -1;
//        }
//
//        if(!checkUserExistInDB(userId)) {
//            return -1;
//        }
//
//        Comment comment = this.commentRepository.findById(commentId).get();
//        Long[] likes = comment.getCommentLikes();
//        if(ArrayUtils.contains(likes, userId)) {
//            log.info("User {} has already liked the comment {}", userId, commentId);
//            return 0;
//        }
//
//        // Add user to likes list of the post
//        likes = ArrayUtils.add(likes, userId);
//        comment.setCommentLikes(likes);
//        this.commentRepository.save(comment);
//
//        log.info("User {} has liked the Comment {}, and add to LIKES list of the Comment", userId, commentId);
//        return 1;
        return this.commentService.likeAComment(commentId, userId);
    }

    @GetMapping("getAllLikesOnComment/{commentId}")
    public Long[] getAllLikesOnComment(@PathVariable Long commentId) {
//        if(!checkCommentExistInDB(commentId)) {
//            return null;
//        }
//
//        Comment comment = this.commentRepository.findById(commentId).get();
//        Long[] likes = comment.getCommentLikes();
//
//        if(ArrayUtils.isEmpty(likes)) {
//            log.info("No LIKES given to the Comment {}", commentId);
//        }
//        else {
//            log.info("No. of Likes given to the Comment {} are: {}", commentId, likes.length);
//        }
//
//        return likes;
        return this.commentService.getAllLikesOnComment(commentId);
    }

    @DeleteMapping("removeAlikeOnComment/{commentId}/{userId}")
    public int removeAlikeOnPost(@PathVariable("commentId") Long commentId, @PathVariable("userId") Long userId) {
//        if(!checkCommentExistInDB(commentId)) {
//            return -1;
//        }
//
//        if(!checkUserExistInDB(userId)) {
//            return -1;
//        }
//
//        Comment comment = this.commentRepository.findById(commentId).get();
//        Long[] likes = comment.getCommentLikes();
//        if(!ArrayUtils.contains(likes, userId)) {
//            log.info("User {} has NOT liked the Comment {}", userId, commentId);
//            return 0;
//        }
//
//        // Remove user from the LIKES list of the post
//        likes = ArrayUtils.removeElement(likes, userId);
//        comment.setCommentLikes(likes);
//        this.commentRepository.save(comment);
//
//        log.info("User {} has removed the liked on Comment {}, and is removed from LIKES list of the Comment", userId, commentId);
//        return 1;
        return this.commentService.removeAlikeOnPost(commentId, userId);
    }

}
