package com.socialising.services.controller;

import com.socialising.services.model.Friend;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.repository.PostRepository;
import com.socialising.services.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/user/")
@Slf4j
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UserController(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @PostMapping("addUser")
    public User addUser(@RequestBody User user) {

        user.setUserId();

        try {
            this.userRepository.save(user);
            log.info("User added to the db");
            return user;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }

    }

    @GetMapping("getAllUserDetails")
    public ArrayList<User> getAllUserDetails() {

        log.info("Total Number of users: {}", userRepository.count());

        return (ArrayList<User>) this.userRepository.findAll();
    }

    @GetMapping("details/{id}")
    public User getUserById(@PathVariable Long id) {

        if(this.userRepository.findById(id).isPresent()) {
            return this.userRepository.findById(id).get();
        }

        log.info("No user present in DB with userid: {}", id);
        return null;
    }

    @GetMapping("getUserByPhoneNumber/{phonenumber}")
    public User getUserByPhoneNumber(@PathVariable String phonenumber) {
        try {
            if(this.userRepository.findByPhoneNumber(phonenumber) != null) {
                return this.userRepository.findByPhoneNumber(phonenumber);
            }
            log.info("No user exists with Phone Number {}", phonenumber);
            return null;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }


    @DeleteMapping("deleteUser/{userid}")
    public int deleteUser(@PathVariable Long userid) {
        if(this.userRepository.findById(userid).isPresent()) {
            Long[] reminderPosts = this.userRepository.findById(userid).get().getReminderPosts();
            this.userRepository.deleteById(userid);
            log.info("User deleted from DB");

            // Delete the user from reminder Posts, confirmed Users list
            if(ArrayUtils.isNotEmpty(reminderPosts)) {
                for(Long reminderPostId: reminderPosts) {
                    Post post = this.postRepository.findById(reminderPostId).get();
                    Long[] confirmedUsers = post.getConfirmedUsers();
                    confirmedUsers = ArrayUtils.removeElement(confirmedUsers, userid);
                    post.setConfirmedUsers(confirmedUsers);
                    this.postRepository.save(post);
                    log.info("user {} removed from the post {} confirmed Users list", userid, reminderPostId);
                }
            }
            return 1;
        } else {
            log.info("No user with this userid present in DB");
            return -1;
        }
    }

    @GetMapping("getFriends/{userid}")
    public ArrayList<User> getFriendsOfUser(@PathVariable Long userid) {

        if(this.userRepository.findById(userid).isPresent()) {
            User user = this.userRepository.findById(userid).get();

            if(!user.getFriends().isEmpty()) {
                ArrayList<User> friends = new ArrayList<>();
                for(Friend friend : user.getFriends()) {
                    friends.add(this.userRepository.findById(friend.getUserId()).get());
                }

                return friends;
            }
            else {
                log.info("User {} has not friends!!", userid);
                return null;
            }
        }

        return null;
    }

    @GetMapping("getReminderPosts/{userid}")
    public Long[] getReminderPosts(@PathVariable Long userid) {
        if(this.userRepository.findById(userid).isPresent()) {
            Long[] reminderPosts = this.userRepository.findById(userid).get().getReminderPosts();

            if(reminderPosts == null) {
                log.info("No Reminder Posts for Post {}", userid);
            } else {
                log.info("Reminder Posts for User {} are {}", userid, reminderPosts.length);
            }

            return reminderPosts;
        }
        log.info("No user with User ID: {}", userid);
        return null;
    }

    @DeleteMapping("deleteReminderPost/{userid}/{postid}")
    public Long[] deleteReminderPostsOfUser(@PathVariable("userid") Long userid, @PathVariable("postid") Long postid) {
        if(this.userRepository.findById(userid).isEmpty()) {
            log.info("User {} does not exists, Please Sign Up!!", userid);
            return null;
        }
        User user = this.userRepository.findById(userid).get();
        if(this.postRepository.findById(postid).isEmpty()) {
            log.info("Post {} is not in User {} Reminer Bucker List", postid, userid);
            return user.getReminderPosts();
        }
        Post post = this.postRepository.findById(postid).get();

        Long[] reminderPosts = user.getReminderPosts();
        Long[] confirmedUsers = post.getConfirmedUsers();

        // Check if post exists in reminderBucket of user
        if(!ArrayUtils.contains(reminderPosts, postid)) {
            log.info("Post {} does not exists in user {} reminder Posts list", postid, userid);
            return reminderPosts;
        }

        // Check if user exists in confirmedUser of post
        if(!ArrayUtils.contains(confirmedUsers, userid)) {
            log.info("User {} does not exists in post {} Confirmed User list", userid, postid);
            log.info("But Post {} exists in user {} reminder posts list. CHECK!!!!!!", postid, userid);
            return null;
        }

        // Delete post from User's reminder Post list
        reminderPosts = ArrayUtils.removeElement(reminderPosts, postid);
        user.setReminderPosts(reminderPosts);
        this.userRepository.save(user);

        // Delete user from Post's confirmed users list
        confirmedUsers = ArrayUtils.removeElement(confirmedUsers, userid);
        post.setConfirmedUsers(confirmedUsers);
        this.postRepository.save(post);

        log.info("User {} Updated Reminder Posts List: {}", userid, user.getReminderPosts());
        log.info("Post {} Updated Confirmed Users List: {}", postid, post.getConfirmedUsers());

        return user.getReminderPosts();
    }

    @GetMapping("getTagsofUser/{userid}")
    public String[] getTagsofUser(@PathVariable Long userid) {
        if(this.userRepository.findById(userid).isPresent()) {
            return this.userRepository.findById(userid).get().getTags();
        }
        return null;
    }

    @PutMapping("updateTagsOfUser/{userid}")
    public String[] updateTagsOfUser(@PathVariable Long userid, @RequestBody String[] newTags) {
        if(this.userRepository.findById(userid).isEmpty()) {
            log.info("User {} does not exist", userid);
            return null;
        }
        User user = this.userRepository.findById(userid).get();
        String[] currentTags = user.getTags();
        user.setTags(newTags);
        this.userRepository.save(user);

        log.info("Old tags of User {}: {}", userid, currentTags);
        log.info("New Tags of user {}: {}", userid, user.getTags());

        return user.getTags();
    }

}
