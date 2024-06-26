package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.Status;
import com.socialising.services.controller.UserController;
import com.socialising.services.model.ChangePasswordRequest;
import com.socialising.services.model.Image;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.repository.ImageRepository;
import com.socialising.services.repository.PostRepository;
import com.socialising.services.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private boolean checkUserExistInDB(Long userid) {
        if(this.userRepository.findById(userid).isPresent()) {
            log.info("User {} exist in DB", userid);
            return true;
        }
        log.info("User {} does not exists, Please Sign Up!!", userid);
        return false;
    }

    private boolean checkImageExistInDB(Long imageId) {
        if(this.imageRepository.findById(imageId).isPresent()) {
            log.info("Image {} exist in DB", imageId);
            return true;
        }
        log.info("Image {} does not exists in DB!!", imageId);
        return false;
    }

    // Add New User to DB
    public User addUser(User user) {

        user.setUserId();
        user.setStatus(Status.ONLINE);

        try {
            this.userRepository.save(user);
            log.info("User added to the db");
            return user;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    // Get all the users in DB
    public ArrayList<User> getAllUserDetails() {

        log.info("Total Number of users: {}", userRepository.count());

        return (ArrayList<User>) this.userRepository.findAll();
    }

    // Get User Details
    public User getUserDetails(String token) {
        String jwtToken = token.substring(7);

        try {
            String username = jwtService.extractUsername(jwtToken);
            return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } catch (Exception e) {
            log.info("JWT Token Error: {}", e.getMessage());
            return null;
        }
    }


    // Get user by ID
    public User getUserById(Long id) {

        if(checkUserExistInDB(id)) {
            return this.userRepository.findById(id).get();
        }

        log.info("No user present in DB with userid: {}", id);
        return null;
    }

    // Get user by Phone Number
    public User getUserByPhoneNumber(String phoneNumber) {
        try {
            if(this.userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
                return this.userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
            log.info("No user exists with Phone Number {}", phoneNumber);
            return null;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    // Delete USER By ID
    public int deleteUser(Long userid) {
        if(checkUserExistInDB(userid)) {
            User user = this.userRepository.findById(userid).get();
            this.userRepository.deleteById(userid);
            log.info("User deleted from DB");

            // Delete the user from reminder Posts, confirmed Users list
            Long[] reminderPosts = user.getReminderPosts();
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

            // Delete User from the friends list of other users
            Long[] friends = user.getFriends();
            if(ArrayUtils.isNotEmpty(friends)) {
                for(Long friendid: friends) {
                    User friend = this.userRepository.findById(friendid).get();
                    Long[] friendFriends = friend.getFriends();
                    friendFriends = ArrayUtils.removeElement(friendFriends, userid);
                    friend.setFriends(friendFriends);
                    this.userRepository.save(friend);
                    log.info("User {} removed from the Friend {} friends list", userid, friendFriends);
                }
            }

            return 1;
        } else {
            log.info("No user with userid {} present in DB", userid);
            return -1;
        }
    }

    // Search User by Word as Usernames
    public List<User> searchUserByWord(String word) {

        return this.userRepository.searchUserByWord(word);
    }

    // Search User by Tag
    public List<User> searchUserByTag(String tag) {

        List<User> allUsers = this.userRepository.findAll();
        List<User> filteredUsers = new ArrayList<>();
        for(User user : allUsers) {
            if(ArrayUtils.contains(user.getTags(), tag)) {
                filteredUsers.add(user);
            }
        }

        log.info("No. of users matching tag: {} are {}", tag, filteredUsers.size());
        return filteredUsers;
    }

    // To Send the Friend Request from User {userRequestId} to User {userid}
    public String sendFriendRequest(Long fromuserid, Long touserid) {
        if(!checkUserExistInDB(fromuserid)) {
            return "User " + String.valueOf(fromuserid) + " does not exist in DB";
        }

        if(!checkUserExistInDB(touserid)) {
            return "User {" + String.valueOf(touserid) + "} does not exist in DB";
        }

        User touser = this.userRepository.findById(touserid).get();
        Long[] friendsOfToUser = touser.getFriends();

        if(ArrayUtils.contains(friendsOfToUser, fromuserid)) {
            log.info("User {} is already friends with User {}", fromuserid, touserid);
            return "User {" + String.valueOf(touserid) + "} already friends with User {" + String.valueOf(fromuserid) + "}";
        }

        Long[] friendRequests = touser.getFriendRequests();

        if(ArrayUtils.contains(friendRequests, fromuserid)) {
            log.info("Friend Request is already sent from user {} to user {}", fromuserid, touserid);
            return "Friend Request Already Sent";
        }

        // Add the user to the friend request list of the requested User
        friendRequests = ArrayUtils.add(friendRequests, fromuserid);
        touser.setFriendRequests(friendRequests);
        this.userRepository.save(touser);
        log.info("User {} added to User {}'s Friend Request list: {}", fromuserid, touserid, touser.getFriendRequests());

        return "Friend request Sent";
    }

    // To Accept the Friend Request of User {userRequestId} to User {userid}
    public String acceptFriendRequest(Long userRequestId, Long userid) {
        if(!checkUserExistInDB(userid)) {
            return "User " + String.valueOf(userid) + " does not exist in DB";
        }

        User user = this.userRepository.findById(userid).get();
        Long[] friendsOfUser = user.getFriends();

        if(ArrayUtils.contains(friendsOfUser, userRequestId)) {
            log.info("User {} is already friends with User {}", userid, userRequestId);
            return "User {" + String.valueOf(userRequestId) + "} already friends with User {" + String.valueOf(userid) + "}";
        }

        Long[] friendRequests = user.getFriendRequests();

        if(!ArrayUtils.contains(friendRequests, userRequestId)) {
            log.info("Friend Request is NOT sent from user {} to user {}", userRequestId, userid);
            return "Friend Request NOT Sent. please send the friend request first!";
        }

        // Removing FROM_USER from TO_USER's friend request list
        friendRequests = ArrayUtils.removeElement(friendRequests, userRequestId);
        user.setFriendRequests(friendRequests);
        this.userRepository.save(user);

        if(!checkUserExistInDB(userRequestId)) {
            return "User {" + String.valueOf(userRequestId) + "} does not exist in DB. ";
        }

        // Add FROM_USER to TO_USER's friends list
        friendsOfUser = ArrayUtils.add(friendsOfUser, userRequestId);
        user.setFriends(friendsOfUser);
        this.userRepository.save(user);

        // Add the TO_USER to FROM_USER's friends list also
        User fromuser = this.userRepository.findById(userRequestId).get();
        Long[] friendsOfFromUser = fromuser.getFriends();
        friendsOfFromUser = ArrayUtils.add(friendsOfFromUser, userid);
        fromuser.setFriends(friendsOfFromUser);
        this.userRepository.save(fromuser);

        log.info("User {} is now Friends with {}", userid, userRequestId);
        return "Friend request accepted";
    }

    // To Remove/Delete the Friend Request from User {userRequestId} to User {userid}
    public String deleteFriendRequest(Long fromuserid, Long touserid) {

        if(!checkUserExistInDB(touserid)) {
            return "User {" + String.valueOf(touserid) + "} does not exist in DB";
        }

        User touser = this.userRepository.findById(touserid).get();
        Long[] friendRequests = touser.getFriendRequests();

        if(!ArrayUtils.contains(friendRequests, fromuserid)) {
            log.info("Friend Request is NOT sent from user {} to user {}", fromuserid, touserid);
            return "Friend Request NOT sent";
        }

        // Remove the user from the friend request list of the requested User
        friendRequests = ArrayUtils.removeElement(friendRequests, fromuserid);
        touser.setFriendRequests(friendRequests);
        this.userRepository.save(touser);

        log.info("User {} removed from User {}'s Friend Request list: {}", fromuserid, touserid, touser.getFriendRequests());
        return "Friend request Rejected/Deleted";
    }

    // Get all the Friend Requests for the USER (User ID)
    public ArrayList<User> getFriendRequestUsers(Long userid) {

        if(checkUserExistInDB(userid)) {
            User user = this.userRepository.findById(userid).get();

            if(ArrayUtils.isNotEmpty(user.getFriendRequests())) {
                Long[] friendRequests = user.getFriendRequests();
                ArrayList<User> userDetails = new ArrayList<>();
                for(Long userReqId : friendRequests) {
                    if(this.userRepository.findById(userReqId).isEmpty()) {
                        log.info("User {} does not exist in DB", userReqId);
                        friendRequests = ArrayUtils.removeElement(friendRequests, userReqId);
                        log.info("User {} removed from User {}'s friends list", userReqId, userid);
                    }
                    else {
                        userDetails.add(this.userRepository.findById(userReqId).get());
                    }
                }
                user.setFriendRequests(friendRequests);
                log.info("User {} has {} friend Requests: {}", userid, user.getFriendRequests().length, friendRequests);
                return userDetails;
            }
            else {
                log.info("User {} has no friend requests!!", userid);
                return null;
            }
        }

        return null;
    }

    // GET all Friends of USER
    public ArrayList<User> getFriendsOfUser(Long userid) {

        if(checkUserExistInDB(userid)) {
            User user = this.userRepository.findById(userid).get();

            if(ArrayUtils.isNotEmpty(user.getFriends())) {
                Long[] friends = user.getFriends();
                ArrayList<User> friendDetails = new ArrayList<>();
                for(Long friendid : friends) {
                    if(this.userRepository.findById(friendid).isEmpty()) {
                        log.info("Friend User {} does not exist in DB", friendid);
                        friends = ArrayUtils.removeElement(friends, friendid);
                        log.info("friend User {} removed from user {} friends list", friendid, userid);
                    }
                    else {
                        friendDetails.add(this.userRepository.findById(friendid).get());
                    }
                }
                user.setFriends(friends);
                log.info("User {} has {} friends: {}", userid, user.getFriends().length, user.getFriends());
                return friendDetails;
            }
            else {
                log.info("User {} has not friends!!", userid);
                return null;
            }
        }

        return null;
    }

    // DELETE a Friend (friendID) from User (User Id)
    public int deleteFriend(Long userid, Long friendid) {

        if(checkUserExistInDB(userid)) {
            User user = this.userRepository.findById(userid).get();

            Long[] friends = user.getFriends();
            friends = ArrayUtils.removeElement(friends, friendid);
            user.setFriends(friends);
            this.userRepository.save(user);

            log.info("friend User {} removed from user {} friends list", friendid, userid);

            if(this.userRepository.findById(friendid).isEmpty()) {
                log.info("Friend User {} does not exist in DB", friendid);
            }
            return 1;
        }

        return -1;
    }

    // Get all the reminder posts
    public Long[] getReminderPosts(Long userid) {
        if(checkUserExistInDB(userid)) {
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

    // DELETE a Reminder Post from User
    public Long[] deleteReminderPostsOfUser(Long userid, Long postid) {
        if(!checkUserExistInDB(userid)) {
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

    // Get all TAGS of the User
    public String[] getTagsofUser(Long userid) {
        if(checkUserExistInDB(userid)) {
            return this.userRepository.findById(userid).get().getTags();
        }
        return null;
    }

    // UPDATE the TAGS of User
    public String[] updateTagsOfUser(Long userid, String[] newTags) {
        if(!checkUserExistInDB(userid)) {
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

    // Add User Display Picture to DB
    public Image addUserDP(Long userId, MultipartFile file) throws Exception {
        if(!checkUserExistInDB(userId)) {
            return null;
        }

        if(file.isEmpty()) {
            log.info("File is empty.");
            return null;
        }

        try {
            // Create Image from File
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Long imageId = Long.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));

            Image image = new Image(imageId, fileName, file.getContentType(), file.getBytes());

            log.info("Image Id: {}", image.getImageId());
            log.info("Image Filename: {}", image.getFilename());
            log.info("Image Type: {}", image.getMimeType());

            // Save Image to DB
            this.imageRepository.save(image);

            // Save ImageId to User
            User user = this.userRepository.findById(userId).get();
            user.setUserDPId(image.getImageId());
            this.userRepository.save(user);

            log.info("Image: [{}] saved for User {}", image, userId);

            return image;
        } catch (Exception e) {
            log.info("Error Uploading Image: {}", e.getMessage());
            return null;
        }

    }

    // GET User DP
    public Image getUserDP(Long userId) throws Exception {
        if(!checkUserExistInDB(userId)) {
            return null;
        }

        try {
            Long userDpId = this.userRepository.findById(userId).get().getUserDPId();

            if(!checkImageExistInDB(userDpId)) {
                log.info("User has no DP. Please add one!");
                return null;
            }

            return this.imageRepository.findById(userDpId).get();
        } catch (Exception e) {
            log.info("Error getting User DP. Please try again!! -> {}", e.getMessage());
            return null;
        }
    }

    // CHAT Based Services for user

    // to connect a User -- set status ONLINE while adding User to DB -- refer addUser()

    // to disconnect user
    public void disconnectUser(Long userId) {
        // check for user existence in DB
        checkUserExistInDB(userId);

        User storedUser = this.userRepository.findById(userId).get();
        storedUser.setStatus(Status.OFFLINE);
        this.userRepository.save(storedUser);
    }

    // to find Connected users
    public List<User> findConnectedUsers() {
        return this.userRepository.findAllByStatus(Status.ONLINE);
    }

    // Change Password
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // If current password is not correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong Password");
        }

        // If the new passowrd is not same as confirmation password
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("New and Confirmed Passwords are not same");
        }

        // Set the new Password and Update the user
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("New Password Set Successfully");
    }
}
