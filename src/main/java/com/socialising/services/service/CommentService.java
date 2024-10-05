package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.Role;
import com.socialising.services.controller.PostController;
import com.socialising.services.model.Comment;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.repository.CommentRepository;
import com.socialising.services.repository.PostRepository;
import com.socialising.services.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

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

    private String getUsernameFromToken(String token) {
        try {
            String username = jwtService.extractUsername(token.substring(7));
            log.info("Requested User is [{}]", username);
            return username;
        } catch (ExpiredJwtException e) {
            log.info("The token has expired. Please Login again!!");
            return "";
        }
    }

    // Add Comment on Post to DB
    public Comment addCommentOnPost(Long postId, Comment newComment, String token) {
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        // Get the username of the comment user from token
        String username = getUsernameFromToken(token);

        if (username.isEmpty()) {
            log.info("User not present");
            return null;
        }

        User user = userRepository.findByUsername(username).get();

        try {
            // Set a unique comment id
            newComment.setCommentId(Long.valueOf(new DecimalFormat("00000000").format(new Random().nextInt(99999999))));

            // Set Post ID
            newComment.setPostId(postId);

            // Set the comment Owner Username
            newComment.setUsername(username);

            // Add New comment to Comment DB
            commentRepository.save(newComment);

            // Add new Comment to Comments list of the post
            Post post = postRepository.findById(postId).get();

            // Get the current Comment Id list of Comments on Post
            Long[] comments = post.getComments();

            // Add the new Comment Id to the list
            comments = ArrayUtils.add(comments, newComment.getCommentId());
            post.setComments(comments);

            // Save the updated the comments list and Post to DB
            postRepository.save(post);

            log.info("New Comment [{}] has been added to Comments DB", newComment.getDescription());
            return newComment;

        } catch (Exception e) {
            log.info("Comment [{}] could not be added. Please try again!!", newComment.getDescription());
            log.info("Error Occurred: [{}]", e.getMessage());
            return null;
        }
    }

    // GET All Comments on Post
    public ArrayList<Comment> getAllCommentsOnPost(Long postId) {
        // Check if Post exists in DB
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        // Get the Post and its Comments list from DB
        Post post = postRepository.findById(postId).get();
        Long[] commentIds = post.getComments();

        // Check if the Comments list of Post is empty
        if(ArrayUtils.isEmpty(commentIds)) {
            log.info("No Comments has been added to the Post [{}]", postId);
            return null;
        }
        log.info("No. of Comments added to the Post [{}] are: [{}]", postId, commentIds.length);

        // Get the comments from comment DB by looping over comment IDs
        ArrayList<Comment> comments = new ArrayList<>();

        // Loop over the Comment list and Get the Comment Object for each Comment Id
        for(Long commentId : commentIds) {
            comments.add(commentRepository.findById(commentId).get());
        }

        // Return the list of Comment Objects of Post
        return comments;
    }

    // DELETE a Comment (Comment ID) on Post
    public int deleteCommentOnPost(Long postId, Long commentId, String token) {
        // Check if Post exists in DB
        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        // Get the Post from DB
        Post post = postRepository.findById(postId).get();

        // Get the list of Comment IDs of Comments on the Post
        Long[] commentIds = post.getComments();

        // Check if the Comment list is empty or not
        if(ArrayUtils.isEmpty(commentIds)) {
            log.info("No comments added to the Post [{}]", postId);
            return -1;
        }

        // Check if the comment exists in the post's comment Ids list
        if(!ArrayUtils.contains(commentIds, commentId)) {
            log.info("Comment [{}] is not added to the Post [{}]. Please check!!", commentId, postId);
            return -1;
        }

        // Check if the comment exists in the DB
        if(checkCommentExistInDB(commentId)) {
            // Get the comment from DB
            Comment comment = commentRepository.findById(commentId).get();

            // Get the username of the comment user from token
            String username = getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).get();

            // Only the Owner of Comment and ADMIN User can delete a comment
            if (!username.equals(comment.getUsername()) && !user.getRole().equals(Role.ADMIN)) {
                log.info("User [{}] is not authorized to Delete the comment [{}]", username, commentId);
                return -1;
            }

            try {
                // remove comment from Comment DB
                commentRepository.deleteById(commentId);
                log.info("Comment [{}] deleted from DB", commentId);

                // Remove comment from the Post comment's list
                commentIds = ArrayUtils.removeElement(commentIds, commentId);
                post.setComments(commentIds);

                // Save the updated Comments list and Post in DB
                postRepository.save(post);

                log.info("Comment [{}] has been deleted from the Comments list of the Post {}", commentId, postId);
                return 1;
            } catch (Exception e) {
                log.info("Exception occurred while deleting comment [{}]", commentId);
                log.info("Error: {}", e.getMessage());
                return 0;
            }
        }

        log.info("Comment [{}] Does not Exist in DB", commentId);
        return -1;
    }

    // LIKE a Comment by User
    public int likeAComment(Long commentId, String token) {

        // Check if Comment exists in DB
        if(!checkCommentExistInDB(commentId)) {
            return -1;
        }

        // Get the username of User who is liking the post
        String username = jwtService.extractUsername(token.substring(7));

        // Get the comment from DB
        Comment comment = commentRepository.findById(commentId).get();

        // Get the likes list on Comment
        String[] likes = comment.getCommentLikes();

        // Check if the user has already liked the comment
        if(ArrayUtils.contains(likes, username)) {
            log.info("User [{}] has already liked the comment {}", username, commentId);
            return 0;
        }

        // Add user to likes list of the post
        likes = ArrayUtils.add(likes, username);
        comment.setCommentLikes(likes);

        // Save the updated Comment to DB
        commentRepository.save(comment);

        log.info("User [{}] has liked the Comment {}, and add to LIKES list of the Comment", username, commentId);
        return 1;
    }

    // GET All LIKES on Comment
    public String[] getAllLikesOnComment(Long commentId) {
        // Check if Comment exists in DB
        if(!checkCommentExistInDB(commentId)) {
            return null;
        }

        // Get the Comment and its likes list from DB
        Comment comment = commentRepository.findById(commentId).get();
        String[] likes = comment.getCommentLikes();

        // Check if the Likes list is Empty
        if(ArrayUtils.isEmpty(likes)) {
            log.info("No LIKES given to the Comment [{}]", commentId);
        }
        else {
            log.info("No. of Likes given to the Comment [{}] are: [{}]", commentId, likes.length);
        }

        return likes != null ? likes : new String[]{};
    }

    // Remove a LIKE on Comment by USER
    public int removeAlikeOnPost(Long commentId, String token) {
        // Check if Comment exists in DB
        if(!checkCommentExistInDB(commentId)) {
            return -1;
        }

        // Get the username of User from token
        String username = jwtService.extractUsername(token.substring(7));

        // Get the Comment and its likes list from DB
        Comment comment = commentRepository.findById(commentId).get();
        String[] likes = comment.getCommentLikes();

        // Check if user has liked the comment
        if(!ArrayUtils.contains(likes, username)) {
            log.info("User [{}] has NOT liked the Comment {}", username, commentId);
            return 0;
        }

        // Remove user from the LIKES list of the post
        likes = ArrayUtils.removeElement(likes, username);
        comment.setCommentLikes(likes);

        // Save the updated Likes list and Comment in DB
        commentRepository.save(comment);

        log.info("User [{}] has removed the liked on Comment {}, and is removed from LIKES list of the Comment", username, commentId);
        return 1;
    }

}
