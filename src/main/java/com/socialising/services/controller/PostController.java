package com.socialising.services.controller;

import com.socialising.services.model.Comment;
import com.socialising.services.model.Image;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.repository.ImageRepository;
import com.socialising.services.repository.PostRepository;
import com.socialising.services.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/post/")
//@Slf4j
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    // Inject the object of Repository using the Bean created in Repository Interface
    public PostController(PostRepository postRepository, ImageRepository imageRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
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

    @PostMapping("addPost")
    public Post addPost(@RequestBody Post post) {

        post.setPostId();
        post.setCreatedTs();

        try {
            this.postRepository.save(post);
            log.info("Post added to db");
            return post;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }

    }

    @GetMapping("getAllPosts")
    public ArrayList<Post> getAllPosts() {

        log.info("Total number of posts: {}", this.postRepository.count());
        return (ArrayList<Post>) this.postRepository.findAll();
    }

    @GetMapping("getPost/{id}")
    public Post getPostById(@PathVariable Long id) {

        return checkPostExistInDB(id) ? this.postRepository.findById(id).get() : null;
    }

    @DeleteMapping("deletePost/{postid}")
    public void deletePost(@PathVariable Long postid) {
        if(checkPostExistInDB(postid)) {
            Long[] confirmedUsers = this.postRepository.findById(postid).get().getConfirmedUsers();
            this.postRepository.deleteById(postid);
            log.info("Post with Post ID: {} deleted from DB", postid);

            // Delete the post from Confirmed User's Reminder Posts list
            if(ArrayUtils.isNotEmpty(confirmedUsers)) {
                for(Long userid: confirmedUsers) {
                    User user = this.userRepository.findById(userid).get();
                    Long[] reminderPosts = user.getReminderPosts();
                    reminderPosts = ArrayUtils.removeElement(reminderPosts, postid);
                    user.setReminderPosts(reminderPosts);
                    this.userRepository.save(user);
                    log.info("Post {} removed from User {} reminder posts list", postid, userid);
                }
            }
        }
    }

    @PostMapping("interestedUserRequest/{postid}/{userid}")
    public int postUserRequest(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {
        if(checkPostExistInDB(postid)) {

            if(!checkUserExistInDB(userid)) {
                return -1;
            }

            Post post = this.postRepository.findById(postid).get();
            Long[] interestedUsers = post.getInterestedUsers() ;
            log.info("Interested Users before: {}", (Object) interestedUsers);

            if (ArrayUtils.contains(interestedUsers, userid)) {
                log.info("User {} exists in Interested Users list for Post {}", userid, postid);
                return interestedUsers.length;
            } else {

                interestedUsers = ArrayUtils.add(interestedUsers, userid);
                post.setInterestedUsers(interestedUsers);
                this.postRepository.save(post);
                log.info("Interested Users for Post: {} are {}", postid, interestedUsers);

                return interestedUsers.length;
            }
        }
        return -1;
    }

    @GetMapping("getInterestedUsers/{postid}")
    public Long[] getInterestedUsers(@PathVariable Long postid) {
        if(checkPostExistInDB(postid)) {

            Long[] interestedUsers = this.postRepository.findById(postid).get().getInterestedUsers();

            if(interestedUsers == null) {
                log.info("No Interested Users for Post {}", postid);
            } else {
                log.info("Interested Users for Post {} are {}", postid, interestedUsers.length);
            }

            return interestedUsers;
        }
        return null;
    }

    @PostMapping("acceptInterestedUser/{postid}/{userid}")
    public int acceptInterestedUser(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {

        if(!checkPostExistInDB(postid)) {
            return -1;
        }

        if(!checkUserExistInDB(userid)) {
            return -1;
        }

        Post post = this.postRepository.findById(postid).get();
        Long[] interestedUsers = post.getInterestedUsers();

        if(!Arrays.asList(interestedUsers).contains(userid)) {
            log.info("User does not exists in Post interested Users list. Please raise request for the post");
            return -1;
        }
        else {
            // delete userid from interestedUsers array
            interestedUsers = ArrayUtils.removeElement(interestedUsers, userid);
            log.info("User {} deleted from the Post {} interested Users list", userid, postid);
        }

        Long[] confirmedUsers = post.getConfirmedUsers();

        if(ArrayUtils.contains(confirmedUsers, userid)) {
            log.info("user {} already exists in confirmed users list for post{}. Check again", userid, postid);
        }
        else {
            // Add userid to confirmedUsers array
            confirmedUsers = ArrayUtils.add(confirmedUsers, userid);
            log.info("User {} added to Post {} confirmed Users List. See you soon fella!!", userid, postid);
        }

        User user = this.userRepository.findById(userid).get();
        Long[] reminderPosts = user.getReminderPosts();

        if(ArrayUtils.contains(reminderPosts, postid)) {
            log.info("Post {} already exists in User {} reminder bucket list of posts!!", postid, userid);
        }
        else {
            // Add post to user's reminder posts list
            reminderPosts = ArrayUtils.add(reminderPosts, postid);
            log.info("Post {} added to User {} reminder Posts list", postid, userid);
        }

        post.setInterestedUsers(interestedUsers);
        post.setConfirmedUsers(confirmedUsers);
        user.setReminderPosts(reminderPosts);

        this.postRepository.save(post);
        this.userRepository.save(user);

        log.info("For Post {}, added user {} to Confirmed Users list {}, removed from Interested Users {}, so the confirmed posts are {}", postid, userid, confirmedUsers, interestedUsers, reminderPosts);
        return 1;
    }

    @PostMapping("rejectInterestedUser/{postid}/{userid}")
    public int rejectInterestedUser(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {
        if(!checkPostExistInDB(postid)) {
            return -1;
        }

        if(!checkUserExistInDB(userid)) {
            return -1;
        }

        Post post = this.postRepository.findById(postid).get();
        Long[] interestedUsers = post.getInterestedUsers();
        interestedUsers = ArrayUtils.removeElement(interestedUsers, userid);
        post.setInterestedUsers(interestedUsers);
        this.postRepository.save(post);

        log.info("User {} removed from Interested Users list {}", userid, interestedUsers);
        return 1;
    }

    @GetMapping("getConfirmedUsers/{postid}")
    public Long[] getConfirmedUsers(@PathVariable Long postid) {
        if(checkPostExistInDB(postid)) {
            Long[] confirmedUsers = this.postRepository.findById(postid).get().getConfirmedUsers();

            if(confirmedUsers == null) {
                log.info("No Confirmed Users for Post {}", postid);
            } else {
                log.info("Confirmed Users for Post {} are {}", postid, confirmedUsers.length);
            }

            return confirmedUsers;
        }
        return null;
    }

    @DeleteMapping("deleteConfirmedUser/{postid}/{userid}")
    public Long[] deleteConfirmedUser(@PathVariable("postid") Long postid, @PathVariable("userid") Long userid) {
        if(!checkPostExistInDB(postid)) {
            log.info("No Post with Post ID {} in DB", postid);
            return null;
        }

        Post post = this.postRepository.findById(postid).get();
        if(this.userRepository.findById(userid).isEmpty()) {
            log.info("No user with User ID {} in DB", userid);
            return post.getConfirmedUsers();
        }

        Long[] confirmedUsers = post.getConfirmedUsers();
        if(!ArrayUtils.contains(confirmedUsers, userid)) {
            log.info("User {} does not exist in Confirmed Users List of Post {}", userid, postid);
            return post.getConfirmedUsers();
        }

        confirmedUsers = ArrayUtils.removeElement(confirmedUsers, userid);
        post.setConfirmedUsers(confirmedUsers);
        this.postRepository.save(post);

        // Delete Post from User's Reminder Posts List
        User user = this.userRepository.findById(userid).get();
        Long[] reminderPosts = user.getReminderPosts();
        reminderPosts = ArrayUtils.removeElement(reminderPosts, postid);
        user.setReminderPosts(reminderPosts);
        this.userRepository.save(user);
        log.info("Post {} removed from User {} Reminder Posts list", postid, userid);

        log.info("User {} removed from Confirmed Users list {}", userid, confirmedUsers);
        return post.getConfirmedUsers();
    }

    @PostMapping("likePost/{postId}/{userId}")
    public int likeAPost(@PathVariable("postId") Long postId, @PathVariable("userId") Long userId) {
        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        if(!checkUserExistInDB(userId)) {
            return -1;
        }

        Post post = this.postRepository.findById(postId).get();
        Long[] likes = post.getLikes();
        if(ArrayUtils.contains(likes, userId)) {
            log.info("User {} has already liked the post {}", userId, postId);
            return 0;
        }

        // Add user to likes list of the post
        likes = ArrayUtils.add(likes, userId);
        post.setLikes(likes);
        this.postRepository.save(post);

        log.info("User {} has liked the post {}, and add to LIKES list of the Post", userId, postId);
        return 1;
    }

    @GetMapping("getAllLikesOnPost/{postId}")
    public Long[] getAllLikesOnPost(@PathVariable("postId") Long postId) {
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        Post post = this.postRepository.findById(postId).get();
        Long[] likes = post.getLikes();

        if(ArrayUtils.isEmpty(likes)) {
            log.info("No LIKES given to the Post {}", postId);
        }
        else {
            log.info("No. of Likes given to the Post {} are: {}", postId, likes.length);
        }

        return likes;
    }

    @DeleteMapping("removeAlikeOnPost/{postId}/{userId}")
    public int removeAlikeOnPost(@PathVariable("postId") Long postId, @PathVariable("userId") Long userId) {
        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        if(!checkUserExistInDB(userId)) {
            return -1;
        }

        Post post = this.postRepository.findById(postId).get();
        Long[] likes = post.getLikes();
        if(!ArrayUtils.contains(likes, userId)) {
            log.info("User {} has NOT liked the post {}", userId, postId);
            return 0;
        }

        // Remove user from the LIKES list of the post
        likes = ArrayUtils.removeElement(likes, userId);
        post.setLikes(likes);
        this.postRepository.save(post);

        log.info("User {} has dis-liked the post {}, and removed from LIKES list of the Post", userId, postId);
        return 1;
    }

//    public void exampleImageUpload() throws Exception {
//        var image = new Image(678L, Files.readAllBytes(Paths.get("backgate college.jpeg")) , "image/jpeg", "backgate college.jpeg");
//
//        System.out.println("Image created not saved: " + image + ", count: " + this.imageRepository.count());
//        this.imageRepository.save(image);
//
//        System.out.println("Image saved: " + "count: " + this.imageRepository.count());
//    }
//
//    @GetMapping("getImage")
//    public ResponseEntity getImage() throws Exception {
//        System.out.println("image request received");
//        exampleImageUpload();
//
//        return ResponseEntity.ok(this.imageRepository.findAll());
//    }
}
