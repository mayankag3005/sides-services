package com.socialising.services.controller;

import com.socialising.services.model.Image;
import com.socialising.services.model.User;
import com.socialising.services.service.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user/")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserDetailsService userDetailsService;

    @Autowired
    public UserController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("addUser")
    public User addUser(@RequestBody User user) {
        return this.userDetailsService.addUser(user);
    }

    // Get All the Users
    @GetMapping("getAllUserDetails")
    public ArrayList<User> getAllUserDetails() {
        return this.userDetailsService.getAllUserDetails();
    }

    // Get user by ID
    @GetMapping("details/{id}")
    public User getUserById(@PathVariable Long id) {
        return this.userDetailsService.getUserById(id);
    }

    // Get user by Phone Number
    @GetMapping("getUserByPhoneNumber/{phonenumber}")
    public User getUserByPhoneNumber(@PathVariable String phonenumber) {
        return this.userDetailsService.getUserByPhoneNumber(phonenumber);
    }

    // DELETE User by ID
    @DeleteMapping("deleteUser/{userid}")
    public int deleteUser(@PathVariable Long userid) {
        return this.userDetailsService.deleteUser(userid);
    }

    //Search User by Word as username
    @GetMapping("searchUsersByName/{word}")
    public List<User> searchUserByWord(@PathVariable String word) {
        return this.userDetailsService.searchUserByWord(word);
    }

    //Search User by Tag
    @GetMapping("searchUsersByTag/{tag}")
    public List<User> searchUserByTag(@PathVariable String tag) {
        return this.userDetailsService.searchUserByTag(tag);
    }

    // To Send the Friend Request from User {userRequestId} to User {userid}
    @PostMapping("sendFriendRequest/{fromuserid}/{touserid}")
    public String sendFriendRequest(@PathVariable("fromuserid") Long fromuserid, @PathVariable("touserid") Long touserid) {
        return this.userDetailsService.sendFriendRequest(fromuserid, touserid);
    }

    // To Accept the Friend Request of User {userRequestId} to User {userid}
    @PostMapping("acceptFriendRequest/{userRequestId}/{userid}")
    public String acceptFriendRequest(@PathVariable("userRequestId") Long userRequestId, @PathVariable("userid") Long userid) {
        return this.userDetailsService.acceptFriendRequest(userRequestId, userid);
    }

    // To Remove/Delete the Friend Request from User {userRequestId} to User {userid}
    @DeleteMapping("deleteFriendRequest/{fromuserid}/{touserid}")
    public String deleteFriendRequest(@PathVariable("fromuserid") Long fromuserid, @PathVariable("touserid") Long touserid) {
        return this.userDetailsService.deleteFriendRequest(fromuserid, touserid);
    }

    @GetMapping("getFriendRequestUsers/{userid}")
    public ArrayList<User> getFriendRequestUsers(@PathVariable Long userid) {
        return this.userDetailsService.getFriendRequestUsers(userid);
    }

    @GetMapping("getFriends/{userid}")
    public ArrayList<User> getFriendsOfUser(@PathVariable Long userid) {
        return this.userDetailsService.getFriendsOfUser(userid);
    }

    @GetMapping("deleteFriend/{userid}/{friendid}")
    public int deleteFriend(@PathVariable("userid") Long userid, @PathVariable("friendid") Long friendid) {
        return this.userDetailsService.deleteFriend(userid, friendid);
    }

    @GetMapping("getReminderPosts/{userid}")
    public Long[] getReminderPosts(@PathVariable Long userid) {
        return this.userDetailsService.getReminderPosts(userid);
    }

    @DeleteMapping("deleteReminderPost/{userid}/{postid}")
    public Long[] deleteReminderPostsOfUser(@PathVariable("userid") Long userid, @PathVariable("postid") Long postid) {
        return this.userDetailsService.deleteReminderPostsOfUser(userid, postid);
    }

    @GetMapping("getTagsofUser/{userid}")
    public String[] getTagsofUser(@PathVariable Long userid) {
        return this.userDetailsService.getTagsofUser(userid);
    }

    @PutMapping("updateTagsOfUser/{userid}")
    public String[] updateTagsOfUser(@PathVariable Long userid, @RequestBody String[] newTags) {
        return this.userDetailsService.updateTagsOfUser(userid, newTags);
    }

    @PostMapping("addUserDP/{userId}")
    public Image addUserDP(@PathVariable Long userId, @RequestBody MultipartFile file) throws Exception {
        return this.userDetailsService.addUserDP(userId, file);
    }

    @GetMapping("getUserDP/{userId}")
    public Image getImage(@PathVariable Long userId) throws Exception {
        return this.userDetailsService.getUserDP(userId);
    }
}
