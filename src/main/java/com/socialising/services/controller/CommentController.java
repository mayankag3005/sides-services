package com.socialising.services.controller;

import com.socialising.services.model.Comment;
import com.socialising.services.model.Post;
import com.socialising.services.repository.CommentRepository;
import com.socialising.services.repository.PostRepository;
import com.socialising.services.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/comment/")
//@Slf4j
public class CommentController {

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final UserRepository userRepository;

    public CommentController(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private boolean checkCommentExistInDB(Long commentId) {
        if(this.commentRepository.findById(commentId).isPresent()) {
            log.info("Comment {} exist in DB", commentId);
            return true;
        }
        log.info("Comment {} does not exists, Please Check!!", commentId);
        return false;
    }

    private boolean checkPostExistInDB(Long postId) {
        if(this.postRepository.findById(postId).isPresent()) {
            log.info("Post {} exist in DB", postId);
            return true;
        }
        log.info("Post {} does not exist in DB", postId);
        return false;
    }

    private boolean checkUserExistInDB(Long userId) {
        if(this.userRepository.findById(userId).isPresent()) {
            log.info("User {} exist in DB", userId);
            return true;
        }
        log.info("User {} does not exists, Please Sign Up!!", userId);
        return false;
    }

    @PostMapping("addCommentOnPost/{postId}")
    public Comment addCommentOnPost(@PathVariable("postId") Long postId, @RequestBody Comment newComment) {
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        // Get the user id of the comment
        Long userId = newComment.getUserId();

        if(!checkUserExistInDB(userId)) {
            return null;
        }

        // Set a unique comment id
        newComment.setCommentId();

        // Add New comment to Comment DB
        this.commentRepository.save(newComment);

        // Add new Comment to Comments list of the post
        Post post = this.postRepository.findById(postId).get();
        Long[] comments = post.getComments();
        comments = ArrayUtils.add(comments, newComment.getCommentId());
        post.setComments(comments);
        this.postRepository.save(post);

        log.info("New Comment [{}] has been added to Comments DB", newComment.getDescription());
        return newComment;
    }

    @GetMapping("getAllCommentsOnPost/{postId}")
    public ArrayList<Comment> getAllCommentsOnPost(@PathVariable("postId") Long postId) {
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        Post post = this.postRepository.findById(postId).get();
        Long[] commentIds = post.getComments();

        if(ArrayUtils.isEmpty(commentIds)) {
            log.info("No Comments has been added to the Post {}", postId);
            return null;
        }
        log.info("No. of Comments added to the Post {} are: {}", postId, commentIds.length);

        // Get the comments from comment DB by looping over comment IDs
        ArrayList<Comment> comments = new ArrayList<>();
        for(Long commentId : commentIds) {
            comments.add(this.commentRepository.findById(commentId).get());
        }
        return comments;
    }

    @DeleteMapping("deleteCommentOnPost/{postId}/{commentId}")
    public int deleteCommentOnPost(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        Post post = this.postRepository.findById(postId).get();
        Long[] commentIds = post.getComments();

        // Remove Comment to Comments list of the post
        if(ArrayUtils.isEmpty(commentIds)) {
            log.info("No comments added to the Post {}", postId);
            return -1;
        }

        // Check if the comment exists in the post's comment Id list
        if(!ArrayUtils.contains(commentIds, commentId)) {
            log.info("Comment {} is not added to the Post {}. Please check!!", commentId, postId);
            return -1;
        }

        // remove comment from comment DB
        this.commentRepository.deleteById(commentId);
        log.info("Comment {} deleted from DB", commentId);

        // Remove comment from the Post comment's list
        commentIds = ArrayUtils.removeElement(commentIds, commentId);
        post.setComments(commentIds);
        this.postRepository.save(post);

        log.info("Comment [{}] has been deleted from the Comments list of the Post {}", commentId, postId);
        return 1;
    }

    @PostMapping("likeComment/{commentId}/{userId}")
    public int likeAPost(@PathVariable("commentId") Long commentId, @PathVariable("userId") Long userId) {
        if(!checkCommentExistInDB(commentId)) {
            return -1;
        }

        if(!checkUserExistInDB(userId)) {
            return -1;
        }

        Comment comment = this.commentRepository.findById(commentId).get();
        Long[] likes = comment.getCommentLikes();
        if(ArrayUtils.contains(likes, userId)) {
            log.info("User {} has already liked the comment {}", userId, commentId);
            return 0;
        }

        // Add user to likes list of the post
        likes = ArrayUtils.add(likes, userId);
        comment.setCommentLikes(likes);
        this.commentRepository.save(comment);

        log.info("User {} has liked the Comment {}, and add to LIKES list of the Comment", userId, commentId);
        return 1;
    }

    @GetMapping("getAllLikesOnComment/{commentId}")
    public Long[] getAllLikesOnComment(@PathVariable Long commentId) {
        if(!checkCommentExistInDB(commentId)) {
            return null;
        }

        Comment comment = this.commentRepository.findById(commentId).get();
        Long[] likes = comment.getCommentLikes();

        if(ArrayUtils.isEmpty(likes)) {
            log.info("No LIKES given to the Comment {}", commentId);
        }
        else {
            log.info("No. of Likes given to the Comment {} are: {}", commentId, likes.length);
        }

        return likes;
    }

    @DeleteMapping("removeAlikeOnComment/{commentId}/{userId}")
    public int removeAlikeOnPost(@PathVariable("commentId") Long commentId, @PathVariable("userId") Long userId) {
        if(!checkCommentExistInDB(commentId)) {
            return -1;
        }

        if(!checkUserExistInDB(userId)) {
            return -1;
        }

        Comment comment = this.commentRepository.findById(commentId).get();
        Long[] likes = comment.getCommentLikes();
        if(!ArrayUtils.contains(likes, userId)) {
            log.info("User {} has NOT liked the Comment {}", userId, commentId);
            return 0;
        }

        // Remove user from the LIKES list of the post
        likes = ArrayUtils.removeElement(likes, userId);
        comment.setCommentLikes(likes);
        this.commentRepository.save(comment);

        log.info("User {} has removed the liked on Comment {}, and is removed from LIKES list of the Comment", userId, commentId);
        return 1;
    }

}
