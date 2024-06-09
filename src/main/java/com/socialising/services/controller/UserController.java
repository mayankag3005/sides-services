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

    @Autowired
    private final UserDetailsService userDetailsService;

    public UserController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("addUser")
    public User addUser(@RequestBody User user) {

//        user.setUserId();
//
//        try {
//            this.userRepository.save(user);
//            log.info("User added to the db");
//            return user;
//        } catch (Exception e) {
//            log.info(e.getMessage());
//            return null;
//        }
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
//        if(checkUserExistInDB(userid)) {
//            User user = this.userRepository.findById(userid).get();
//            this.userRepository.deleteById(userid);
//            log.info("User deleted from DB");
//
//            // Delete the user from reminder Posts, confirmed Users list
//            Long[] reminderPosts = user.getReminderPosts();
//            if(ArrayUtils.isNotEmpty(reminderPosts)) {
//                for(Long reminderPostId: reminderPosts) {
//                    Post post = this.postRepository.findById(reminderPostId).get();
//                    Long[] confirmedUsers = post.getConfirmedUsers();
//                    confirmedUsers = ArrayUtils.removeElement(confirmedUsers, userid);
//                    post.setConfirmedUsers(confirmedUsers);
//                    this.postRepository.save(post);
//                    log.info("user {} removed from the post {} confirmed Users list", userid, reminderPostId);
//                }
//            }
//
//            // Delete User from the friends list of other users
//            Long[] friends = user.getFriends();
//            if(ArrayUtils.isNotEmpty(friends)) {
//                for(Long friendid: friends) {
//                    User friend = this.userRepository.findById(friendid).get();
//                    Long[] friendFriends = friend.getFriends();
//                    friendFriends = ArrayUtils.removeElement(friendFriends, userid);
//                    friend.setFriends(friendFriends);
//                    this.userRepository.save(friend);
//                    log.info("User {} removed from the Friend {} friends list", userid, friendFriends);
//                }
//            }
//
//            return 1;
//        } else {
//            log.info("No user with userid {} present in DB", userid);
//            return -1;
//        }
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
//        if(!checkUserExistInDB(fromuserid)) {
//            return "User " + String.valueOf(fromuserid) + " does not exist in DB";
//        }
//
//        if(!checkUserExistInDB(touserid)) {
//            return "User {" + String.valueOf(touserid) + "} does not exist in DB";
//        }
//
//        User touser = this.userRepository.findById(touserid).get();
//        Long[] friendsOfToUser = touser.getFriends();
//
//        if(ArrayUtils.contains(friendsOfToUser, fromuserid)) {
//            log.info("User {} is already friends with User {}", fromuserid, touserid);
//            return "User {" + String.valueOf(touserid) + "} already friends with User {" + String.valueOf(fromuserid) + "}";
//        }
//
//        Long[] friendRequests = touser.getFriendRequests();
//
//        if(ArrayUtils.contains(friendRequests, fromuserid)) {
//            log.info("Friend Request is already sent from user {} to user {}", fromuserid, touserid);
//            return "Friend Request Already Sent";
//        }
//
//        // Add the user to the friend request list of the requested User
//        friendRequests = ArrayUtils.add(friendRequests, fromuserid);
//        touser.setFriendRequests(friendRequests);
//        this.userRepository.save(touser);
//        log.info("User {} added to User {}'s Friend Request list: {}", fromuserid, touserid, touser.getFriendRequests());
//
//        return "Friend request Sent";
        return this.userDetailsService.sendFriendRequest(fromuserid, touserid);
    }

    // To Accept the Friend Request of User {userRequestId} to User {userid}
    @PostMapping("acceptFriendRequest/{userRequestId}/{userid}")
    public String acceptFriendRequest(@PathVariable("userRequestId") Long userRequestId, @PathVariable("userid") Long userid) {
//        if(!checkUserExistInDB(userid)) {
//            return "User " + String.valueOf(userid) + " does not exist in DB";
//        }
//
//        User user = this.userRepository.findById(userid).get();
//        Long[] friendsOfUser = user.getFriends();
//
//        if(ArrayUtils.contains(friendsOfUser, userRequestId)) {
//            log.info("User {} is already friends with User {}", userid, userRequestId);
//            return "User {" + String.valueOf(userRequestId) + "} already friends with User {" + String.valueOf(userid) + "}";
//        }
//
//        Long[] friendRequests = user.getFriendRequests();
//
//        if(!ArrayUtils.contains(friendRequests, userRequestId)) {
//            log.info("Friend Request is NOT sent from user {} to user {}", userRequestId, userid);
//            return "Friend Request NOT Sent. please send the friend request first!";
//        }
//
//        // Removing FROM_USER from TO_USER's friend request list
//        friendRequests = ArrayUtils.removeElement(friendRequests, userRequestId);
//        user.setFriendRequests(friendRequests);
//        this.userRepository.save(user);
//
//        if(!checkUserExistInDB(userRequestId)) {
//            return "User {" + String.valueOf(userRequestId) + "} does not exist in DB. ";
//        }
//
//        // Add FROM_USER to TO_USER's friends list
//        friendsOfUser = ArrayUtils.add(friendsOfUser, userRequestId);
//        user.setFriends(friendsOfUser);
//        this.userRepository.save(user);
//
//        // Add the TO_USER to FROM_USER's friends list also
//        User fromuser = this.userRepository.findById(userRequestId).get();
//        Long[] friendsOfFromUser = fromuser.getFriends();
//        friendsOfFromUser = ArrayUtils.add(friendsOfFromUser, userid);
//        fromuser.setFriends(friendsOfFromUser);
//        this.userRepository.save(fromuser);
//
//        log.info("User {} is now Friends with {}", userid, userRequestId);
//        return "Friend request accepted";
        return this.userDetailsService.acceptFriendRequest(userRequestId, userid);
    }

    // To Remove/Delete the Friend Request from User {userRequestId} to User {userid}
    @DeleteMapping("deleteFriendRequest/{fromuserid}/{touserid}")
    public String deleteFriendRequest(@PathVariable("fromuserid") Long fromuserid, @PathVariable("touserid") Long touserid) {

//        if(!checkUserExistInDB(touserid)) {
//            return "User {" + String.valueOf(touserid) + "} does not exist in DB";
//        }
//
//        User touser = this.userRepository.findById(touserid).get();
//        Long[] friendRequests = touser.getFriendRequests();
//
//        if(!ArrayUtils.contains(friendRequests, fromuserid)) {
//            log.info("Friend Request is NOT sent from user {} to user {}", fromuserid, touserid);
//            return "Friend Request NOT sent";
//        }
//
//        // Remove the user from the friend request list of the requested User
//        friendRequests = ArrayUtils.removeElement(friendRequests, fromuserid);
//        touser.setFriendRequests(friendRequests);
//        this.userRepository.save(touser);
//
//        log.info("User {} removed from User {}'s Friend Request list: {}", fromuserid, touserid, touser.getFriendRequests());
//        return "Friend request Rejected/Deleted";
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

//        if(checkUserExistInDB(userid)) {
//            User user = this.userRepository.findById(userid).get();
//
//            Long[] friends = user.getFriends();
//            friends = ArrayUtils.removeElement(friends, friendid);
//            user.setFriends(friends);
//            this.userRepository.save(user);
//
//            log.info("friend User {} removed from user {} friends list", friendid, userid);
//
//            if(this.userRepository.findById(friendid).isEmpty()) {
//                log.info("Friend User {} does not exist in DB", friendid);
//            }
//            return 1;
//        }
//
//        return -1;
        return this.userDetailsService.deleteFriend(userid, friendid);
    }

    @GetMapping("getReminderPosts/{userid}")
    public Long[] getReminderPosts(@PathVariable Long userid) {

        return this.userDetailsService.getReminderPosts(userid);
    }

    @DeleteMapping("deleteReminderPost/{userid}/{postid}")
    public Long[] deleteReminderPostsOfUser(@PathVariable("userid") Long userid, @PathVariable("postid") Long postid) {
//        if(!checkUserExistInDB(userid)) {
//            return null;
//        }
//        User user = this.userRepository.findById(userid).get();
//        if(this.postRepository.findById(postid).isEmpty()) {
//            log.info("Post {} is not in User {} Reminer Bucker List", postid, userid);
//            return user.getReminderPosts();
//        }
//        Post post = this.postRepository.findById(postid).get();
//
//        Long[] reminderPosts = user.getReminderPosts();
//        Long[] confirmedUsers = post.getConfirmedUsers();
//
//        // Check if post exists in reminderBucket of user
//        if(!ArrayUtils.contains(reminderPosts, postid)) {
//            log.info("Post {} does not exists in user {} reminder Posts list", postid, userid);
//            return reminderPosts;
//        }
//
//        // Check if user exists in confirmedUser of post
//        if(!ArrayUtils.contains(confirmedUsers, userid)) {
//            log.info("User {} does not exists in post {} Confirmed User list", userid, postid);
//            log.info("But Post {} exists in user {} reminder posts list. CHECK!!!!!!", postid, userid);
//            return null;
//        }
//
//        // Delete post from User's reminder Post list
//        reminderPosts = ArrayUtils.removeElement(reminderPosts, postid);
//        user.setReminderPosts(reminderPosts);
//        this.userRepository.save(user);
//
//        // Delete user from Post's confirmed users list
//        confirmedUsers = ArrayUtils.removeElement(confirmedUsers, userid);
//        post.setConfirmedUsers(confirmedUsers);
//        this.postRepository.save(post);
//
//        log.info("User {} Updated Reminder Posts List: {}", userid, user.getReminderPosts());
//        log.info("Post {} Updated Confirmed Users List: {}", postid, post.getConfirmedUsers());
//
//        return user.getReminderPosts();
        return this.userDetailsService.deleteReminderPostsOfUser(userid, postid);
    }

    @GetMapping("getTagsofUser/{userid}")
    public String[] getTagsofUser(@PathVariable Long userid) {

        return this.userDetailsService.getTagsofUser(userid);
    }

    @PutMapping("updateTagsOfUser/{userid}")
    public String[] updateTagsOfUser(@PathVariable Long userid, @RequestBody String[] newTags) {
//        if(!checkUserExistInDB(userid)) {
//            return null;
//        }
//        User user = this.userRepository.findById(userid).get();
//        String[] currentTags = user.getTags();
//        user.setTags(newTags);
//        this.userRepository.save(user);
//
//        log.info("Old tags of User {}: {}", userid, currentTags);
//        log.info("New Tags of user {}: {}", userid, user.getTags());
//
//        return user.getTags();
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
