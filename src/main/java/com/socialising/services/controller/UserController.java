package com.socialising.services.controller;

import com.socialising.services.model.Friend;
import com.socialising.services.model.User;
import com.socialising.services.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/user/")
@Slf4j
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("details")
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

    @DeleteMapping("deleteUser/{userid}")
    public void deleteUser(@PathVariable Long userid) {
        if(this.userRepository.findById(userid).isPresent()) {
            this.userRepository.deleteById(userid);

            log.info("User deleted from DB");
        } else {
            log.info("No user with this userid present in DB");
        }
    }

    @GetMapping("getFriends/{userid}")
    public ArrayList<User> getFriendsOfUser(@PathVariable Long userid) {

        if(this.userRepository.findById(userid).isPresent()) {
            User user = this.userRepository.findById(userid).get();

            ArrayList<User> friends = new ArrayList<>();

            for(Friend friend : user.getFriends()) {
                friends.add(this.userRepository.findById(friend.getUserId()).get());
            }

            return friends;
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

    @GetMapping("getTagsofUser/{userid}")
    public String[] getTagsofUser(@PathVariable Long userid) {
        if(this.userRepository.findById(userid).isPresent()) {
            return this.userRepository.findById(userid).get().getTags();
        }
        return null;
    }

}
