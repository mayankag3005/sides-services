package com.socialising.services.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.socialising.services.dto.PostDTO;
import com.socialising.services.dto.UserDTO;
import com.socialising.services.model.ChangePasswordRequest;
import com.socialising.services.model.Image;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.service.UserService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user/")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userDetailsService;

    private boolean checkTokenValidity(String token) {
        if (!token.contains("Bearer ") || token.length() < 8) {
            log.info("It is an invalid token. Pass the valid token!");
            return false;
        }
        return true;
    }

    /** Adding User without Register is not required now **/
    // Add a User manually (Done by ADMIN only)
//    @PostMapping("addUser")
//    @PreAuthorize("hasAuthority('admin:create')")
//    public User addUser(@RequestBody UserDTO userDto, @RequestHeader("Authorization") String token) {
//        return this.userDetailsService.addUser(userDto, token);
//    }

    // Get All the Users
    @GetMapping("getAllUserDetails")
    @PreAuthorize("hasAuthority('admin:read')")
    public ArrayList<User> getAllUserDetails() {
        return this.userDetailsService.getAllUserDetails();
    }

    // Get User details
    @GetMapping("getUserDetails")
    public User getUserDetails(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return userDetailsService.getUserDetails(token);
    }

    // Get User by Username
    @GetMapping("detailsByUsername/{username}")
    public User getUserById(@PathVariable String username) {
        return this.userDetailsService.getUserByUsername(username);
    }

    // Get user by ID
    @GetMapping("detailsById/{id}")
    @PreAuthorize("hasAuthority('admin:read')")
    public User getUserById(@PathVariable Long id) {
        return this.userDetailsService.getUserById(id);
    }

    // Get user by Phone Number
    @GetMapping("detailsByPhoneNumber/{phoneNumber}")
    @PreAuthorize("hasAuthority('admin:read')")
    public User getUserByPhoneNumber(@PathVariable String phoneNumber) {
        return this.userDetailsService.getUserByPhoneNumber(phoneNumber);
    }

     // Update user details
    @PatchMapping("updateUserDetails")
    public UserDTO updateUserDetails(@RequestBody UserDTO userDto, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.updateUserDetailsExceptUsernamePasswordAndDP(userDto, token);
    }

    // DELETE User by ID
    @DeleteMapping("deleteUser/{userId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public int deleteUser(@PathVariable Long userId) {
        return this.userDetailsService.deleteUser(userId);
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

    //Search User by Tag Keyword
    @GetMapping("searchUsersByTagKey/{keyword}")
    public List<User> searchUsersByTagContaining(@PathVariable String keyword) {
        return this.userDetailsService.searchUsersByTagContaining(keyword);
    }

    // To Send the Friend Request from User to User {toUsername}
    @PostMapping("sendFriendRequest/{username}")
    public String sendFriendRequest(@PathVariable("username") String toUsername, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return "Token Invalid";
        }
        return this.userDetailsService.sendFriendRequest(toUsername, token);
    }

    // To Accept the Friend Request of User {userRequestId} to User {userid}
    @PostMapping("acceptFriendRequest/{username}")
    public String acceptFriendRequest(@PathVariable("username") String fromUsername, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return "Token Invalid";
        }
        return this.userDetailsService.acceptFriendRequest(fromUsername, token);
    }

    // To Reject the Friend Request from User {fromUsername}
    @DeleteMapping("rejectFriendRequest/{friendRequestUsername}")
    public String rejectFriendRequest(@PathVariable("friendRequestUsername") String fromUsername, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return "Token invalid";
        }
        return this.userDetailsService.rejectFriendRequest(fromUsername, token);
    }

    // To Delete the Friend Request sent to User {toUsername}
    @DeleteMapping("deleteFriendRequest/{toUsername}")
    public String deleteFriendRequest(@PathVariable("toUsername") String toUsername, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return "Token invalid";
        }
        return this.userDetailsService.deleteFriendRequest(toUsername, token);
    }

    @GetMapping("getFriendRequestUsers")
    public String[] getFriendRequestUsers(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.getFriendRequestUsers(token);
    }

    @GetMapping("getFriendsRequested")
    public String[] getFriendsRequestedByUser(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.getFriendsRequested(token);
    }

    @GetMapping("getFriends")
    public String[] getFriendsOfUser(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.getFriendsOfUser(token);
    }

    @DeleteMapping("deleteFriend/{friendUsername}")
    public int deleteFriend(@PathVariable("friendUsername") String friendUsername, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.userDetailsService.deleteFriend(friendUsername, token);
    }

    @GetMapping("getPosts")
    public List<Post> getPostsOfUser(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.getPostsOfUser(token);
    }

    @GetMapping("getRequestedPosts")
    public List<Post> getRequestedPostsOfUser(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.getRequestedPostsOfUser(token);
    }

    @GetMapping("getReminderPosts")
    public List<Post> getReminderPosts(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.getReminderPosts(token);
    }

    @GetMapping("getUpcomingEvents")
    public ResponseEntity<ArrayList<PostDTO>> getUpcomingEvents(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return new ResponseEntity<>(this.userDetailsService.getUpcomingEvents(token), HttpStatus.OK);
    }

    @DeleteMapping("deleteReminderPost/{postId}")
    public List<Post> deleteReminderPostsOfUser(@PathVariable("postId") Long postId, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.deleteReminderPostsOfUser(postId, token);
    }

    @GetMapping("getTagsofUser")
    public String[] getTagsOfUser(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.getTagsOfUser(token);
    }

    @PutMapping("updateTagsOfUser")
    public String[] updateTagsOfUser(@RequestBody String[] newTags, @RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.updateTagsOfUser(newTags, token);
    }

    @PostMapping("addUserDP")
    public Image addUserDP(@RequestBody MultipartFile file, @RequestHeader("Authorization") String token) throws Exception {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.addUserDP(file, token);
    }

    @GetMapping("getUserDP")
    public Image getImage(@RequestHeader("Authorization") String token) throws Exception {
        if (!checkTokenValidity(token)) {
            return null;
        }
        return this.userDetailsService.getUserDP(token);
    }

    @DeleteMapping("removeDP")
    public int removeUserDP(@RequestHeader("Authorization") String token) {
        if (!checkTokenValidity(token)) {
            return -1;
        }
        return this.userDetailsService.removeUserDP(token);
    }

/***    CHAT based APIs  ***/
//    @MessageMapping("/user.addUser")
//    @SendTo("/user/public")
//    public User connectUser(@Payload User user, @RequestHeader("Authorization") String token) {
//        if (!checkTokenValidity(token)) {
//            return null;
//        }
//        return this.userDetailsService.addUser(user, token);
//    }

    // All the users subscribed to /user/public will get to know that the user has disconnected
    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public User disconnect(@Payload User user) {
        this.userDetailsService.disconnectUser(user.getUserId());

        return user;
    }

    @GetMapping("/onlineUsers")
    public ResponseEntity<List<User>> getAllOnlineUsers() {
        return ResponseEntity.ok(this.userDetailsService.findConnectedUsers());
    }

    // Change Password
    @PatchMapping("changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        userDetailsService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
