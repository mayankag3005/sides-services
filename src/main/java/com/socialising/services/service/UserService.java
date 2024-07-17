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
import com.socialising.services.repository.TagRepository;
import com.socialising.services.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
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
    private final TagRepository tagRepository;
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

    private boolean checkUserExistInDBWithUsername(String username) {
        if(userRepository.findByUsername(username).isPresent()) {
            log.info("User [{}] exist in DB", username);
            return true;
        }
        log.info("User [{}] does not exists, Please Sign Up!!", username);
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

    private boolean checkImageExistInDB(Long imageId) {
        if(this.imageRepository.findById(imageId).isPresent()) {
            log.info("Image {} exist in DB", imageId);
            return true;
        }
        log.info("Image {} does not exists in DB!!", imageId);
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

    // Add New User to DB
    public User addUser(User user) {
        Long userId = Long.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
        user.setUserId(userId);
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

        return new ArrayList<User>(userRepository.findAll());
    }

    // Get User Details
    public User getUserDetails(String token) {
        try {
            String username = getUsernameFromToken(token);
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

    // Get User by username
    public User getUserByUsername(String username) {
        if(checkUserExistInDBWithUsername(username)) {
            return userRepository.findByUsername(username).get();
        }
        log.info("No user present in DB with username: {}", username);
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
            List<Post> reminderPosts = user.getReminderPosts();
            if(!reminderPosts.isEmpty()) {
                for(Post reminderPost: reminderPosts) {
                    List<User> confirmedUsers = reminderPost.getConfirmedUsers();
                    confirmedUsers.remove(user);
                    reminderPost.setConfirmedUsers(confirmedUsers);
                    this.postRepository.save(reminderPost);
                    log.info("User [{}] removed from the post confirmed Users list", user.getUsername());
                }
            }

            // Delete User from the friends list of other users
            String[] friends = user.getFriends();
            if(ArrayUtils.isNotEmpty(friends)) {
                for(String friendUsername: friends) {
                    User friend = userRepository.findByUsername(friendUsername).get();
                    String[] friendsOfFriend = friend.getFriends();
                    friendsOfFriend = ArrayUtils.removeElement(friendsOfFriend, user.getUsername());
                    friend.setFriends(friendsOfFriend);
                    userRepository.save(friend);
                    log.info("User [{}] removed from the Friend [{}] friends list", user.getUsername(), friendUsername);
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
    /** fromUser is the current user **/
    public String sendFriendRequest(String toUsername, String token) {

        // Get the username of the user who is sending the request
        String fromUsername = getUsernameFromToken(token);

        // Check if TO_USER ( who has been requested ) exists in DB
        if(!checkUserExistInDBWithUsername(toUsername)) {
            return "User [" + toUsername + "] does not exist in DB";
        }

        // Get the user and friends of toUser
        User toUser = userRepository.findByUsername(toUsername).get();
        String[] friendsOfToUser = toUser.getFriends();

        // Check if the toUser is already friends with fromUser
        if(ArrayUtils.contains(friendsOfToUser, fromUsername)) {
            log.info("User [{}] is already friends with User [{}]", toUsername, fromUsername);
            return "User [" + toUsername + "] is already friends with User [" + fromUsername + "]";
        }

        // Get the friend requests of the toUser
        String[] friendRequestsOfToUser = toUser.getFriendRequests();

        // Check if the friend request is already sent from fromUser to toUser
        // So, check if the fromUsername already exists in friend requests list of toUser
        if(ArrayUtils.contains(friendRequestsOfToUser, fromUsername)) {
            log.info("Friend Request is already sent from user [{}] to user [{}]", fromUsername, toUsername);
            return "Friend Request Already Sent";
        }

        // Add the user to the friend request list of the toUser
        friendRequestsOfToUser = ArrayUtils.add(friendRequestsOfToUser, fromUsername);
        toUser.setFriendRequests(friendRequestsOfToUser);
        userRepository.save(toUser);
        log.info("User [{}] added to User [{}'s] Friend Request list: {}", fromUsername, toUsername, toUser.getFriendRequests());

        return "Friend request Sent";
    }

    // To Accept the Friend Request of User {fromUsername} to User
    /** toUser is the current user **/
    public String acceptFriendRequest(String fromUsername, String token) {

        // Get the toUser who has to accept the friend Request
        String toUsername = getUsernameFromToken(token);
        User toUser = userRepository.findByUsername(toUsername).get();

        // Get the friend Request list and friends list of TO_USER
        String[] friendRequestsOfToUser = toUser.getFriendRequests();
        String[] friendsOfToUser = toUser.getFriends();

        // Check if fromUsername already exists in friends list of TO_USER
        if(ArrayUtils.contains(friendsOfToUser, fromUsername)) {
            log.info("User [{}] is already friends with to user [{}]", fromUsername, toUsername);
            return "Already Friends";
        }

        // Check if fromUsername exists in friend Requests list of TO_USER
        if(!ArrayUtils.contains(friendRequestsOfToUser, fromUsername)) {
            log.info("Friend Request is NOT sent from user [{}] to user [{}]", fromUsername, toUsername);
            return "Friend Request NOT Sent";
        }

        // Remove the FROM_USER username from friend Requests list of TO_USER
        friendRequestsOfToUser = ArrayUtils.removeElement(friendRequestsOfToUser, fromUsername);
        toUser.setFriendRequests(friendRequestsOfToUser);

        // Check if fromUser exists in DB
        if(!checkUserExistInDBWithUsername(fromUsername)) {
            // Save updated friend Requests list of TO_USER in DB and return
            userRepository.save(toUser);
            return "User [" + fromUsername + "] does not exist in DB";
        }

        // Get the fromUser user from DB
        User fromUser = userRepository.findByUsername(fromUsername).get();

        // Get the friends of fromUser
        String[] friendsOfFromUser = fromUser.getFriends();

        // Add FROM_USER to TO_USER's friends list
        friendsOfToUser = ArrayUtils.add(friendsOfToUser, fromUsername);
        toUser.setFriends(friendsOfToUser);

        // Add the TO_USER to FROM_USER's friends list also
        friendsOfFromUser = ArrayUtils.add(friendsOfFromUser, toUsername);
        fromUser.setFriends(friendsOfFromUser);

        // Save the users back to DB
        userRepository.save(toUser);
        userRepository.save(fromUser);

        log.info("User [{}] is now Friends with [{}]", toUsername, fromUsername);
        return "Friend request accepted";
    }

    // To Remove/Delete the Friend Request from User {userRequestId} to User {userid}
    /** toUser is the current user **/
    public String deleteFriendRequest(String fromUsername, String token) {

        // Get the toUser who has to reject the friend Request
        String toUsername = getUsernameFromToken(token);
        User toUser = userRepository.findByUsername(toUsername).get();

        // Get the friend Request list of TO_USER
        String[] friendRequestsOfToUser = toUser.getFriendRequests();

        // Check if FROM_USER has sent the request to TO_USER
        if(!ArrayUtils.contains(friendRequestsOfToUser, fromUsername)) {
            log.info("Friend Request is NOT sent from User [{}] to User [{}]", fromUsername, toUsername);
            return "Friend Request NOT sent";
        }

        // Remove the FROM_USER from the friend request list of the TO_USER
        friendRequestsOfToUser = ArrayUtils.removeElement(friendRequestsOfToUser, fromUsername);
        toUser.setFriendRequests(friendRequestsOfToUser);
        userRepository.save(toUser);

        log.info("User [{}] removed from User [{}'s] Friend Request list: [{}]", fromUsername, toUsername, toUser.getFriendRequests());
        return "Friend request Deleted";
    }

    // Get all the Friend Requests of the USER
    public String[] getFriendRequestUsers(String token) {

        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).get();
        log.info("Friend Requests for User [{}] are: {}", username, user.getFriendRequests());
        return user.getFriendRequests();

//        if(ArrayUtils.isNotEmpty(user.getFriendRequests())) {
//            Long[] friendRequests = user.getFriendRequests();
//            ArrayList<User> userDetails = new ArrayList<>();
//            for(Long userReqId : friendRequests) {
//                if(this.userRepository.findById(userReqId).isEmpty()) {
//                    log.info("User {} does not exist in DB", userReqId);
//                    friendRequests = ArrayUtils.removeElement(friendRequests, userReqId);
//                    log.info("User {} removed from User {}'s friends list", userReqId, userid);
//                }
//                else {
//                    userDetails.add(this.userRepository.findById(userReqId).get());
//                }
//            }
//            user.setFriendRequests(friendRequests);
//            log.info("User {} has {} friend Requests: {}", userid, user.getFriendRequests().length, friendRequests);
//            return userDetails;
//        }
//        else {
//            log.info("User {} has no friend requests!!", userid);
//            return new ArrayList<>();
//        }
    }

    // GET all Friends of USER
    public String[] getFriendsOfUser(String token) {

        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).get();
        log.info("Friends for User [{}] are: {}", username, user.getFriends());
        return user.getFriends();

//        if(ArrayUtils.isNotEmpty(user.getFriends())) {
//            Long[] friends = user.getFriends();
//            ArrayList<User> friendDetails = new ArrayList<>();
//            for(Long friendid : friends) {
//                if(this.userRepository.findById(friendid).isEmpty()) {
//                    log.info("Friend User {} does not exist in DB", friendid);
//                    friends = ArrayUtils.removeElement(friends, friendid);
//                    log.info("friend User {} removed from user {} friends list", friendid, userid);
//                }
//                else {
//                    friendDetails.add(this.userRepository.findById(friendid).get());
//                }
//            }
//            user.setFriends(friends);
//            log.info("User {} has {} friends: {}", userid, user.getFriends().length, user.getFriends());
//            return friendDetails;
//        }
//        else {
//            log.info("User {} has not friends!!", userid);
//            return null;
//        }
    }

    // DELETE a Friend (friendID) from User (User Id)
    public int deleteFriend(String friendUsername, String token) {

        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).get();

        // Get the friend's list of the user
        String[] friendsOfUser = user.getFriends();

        // check if both the users are friend or not
        if(!ArrayUtils.contains(friendsOfUser, friendUsername)) {
            log.info("Friend User [{}] is not friends with User [{}]", friendUsername, username);
            return -1;
        }

        // Remove friend Username from Friend's list of User
        friendsOfUser = ArrayUtils.removeElement(friendsOfUser, friendUsername);
        user.setFriends(friendsOfUser);
        userRepository.save(user);
        log.info("Friend User [{}] removed from user [{}'s] friends list", friendUsername, username);

        // Remove username from Friend's list of Friend User
        if(checkUserExistInDBWithUsername(friendUsername)) {
            User friendUser = userRepository.findByUsername(friendUsername).get();
            String[] friendsOfFriendUser = friendUser.getFriends();
            friendsOfFriendUser = ArrayUtils.removeElement(friendsOfFriendUser, username);
            friendUser.setFriends(friendsOfFriendUser);
            userRepository.save(friendUser);
            log.info("User [{}] removed from Friend user [{}'s] friends list", username, friendUsername);
        }  else {
            log.info("Friend User [{}] does not exist in DB", friendUsername);
        }

        return 1;
    }

    public List<Post> getPostsOfUser(String token) {

        String username = getUsernameFromToken(token);
        List<Post> posts = userRepository.findByUsername(username).get().getPosts();
        log.info("Number of Posts for User [{}] are {}", username, posts.size());
        return posts;

    }

    public List<Post> getRequestedPostsOfUser(String token) {
        String username = getUsernameFromToken(token);
        List<Post> requestedPosts = userRepository.findByUsername(username).get().getRequestedPosts();
        log.info("Number of Requested Posts for User [{}] are {}", username, requestedPosts.size());
        return requestedPosts;
    }

    // Get all the reminder posts
    public List<Post> getReminderPosts(String token) {

        String username = getUsernameFromToken(token);
        List<Post> reminderPosts = userRepository.findByUsername(username).get().getReminderPosts();

        if(reminderPosts.isEmpty()) {
            log.info("No Reminder Posts for User [{}]", username);
        } else {
            log.info("Reminder Posts for User [{}] are {}", username, reminderPosts.size());
        }

        return reminderPosts;
    }

    // DELETE a Reminder Post from User
    public List<Post> deleteReminderPostsOfUser(Long postId, String token) {
        String username = getUsernameFromToken(token);

        // Get the user from DB
        User user = userRepository.findByUsername(username).get();

        // Check if Post exists in DB
        if(!checkPostExistInDB(postId)) {
            return user.getReminderPosts();
        }
        Post post = this.postRepository.findById(postId).get();

        List<Post> reminderPosts = user.getReminderPosts();
        List<User> confirmedUsers = post.getConfirmedUsers();

        // Check if post exists in reminderBucket of user
        if(!reminderPosts.contains(post)) {
            log.info("Post {} does not exists in user [{}] reminder Posts list", postId, user.getUsername());
            return reminderPosts;
        }

        // Check if user exists in confirmedUser of post
        if(!confirmedUsers.contains(user)) {
            log.info("User [{}] does not exists in post {} Confirmed User list", user.getUsername(), postId);
            log.info("But Post {} exists in User's [{}] reminder posts list. CHECK!!!!!!", postId, user.getUsername());
            return null;
        }

        // Delete post from User's reminder Post list
        reminderPosts.remove(post);
        user.setReminderPosts(reminderPosts);
        userRepository.save(user);

        // Delete user from Post's confirmed users list
        confirmedUsers.remove(user);
        post.setConfirmedUsers(confirmedUsers);
        postRepository.save(post);

        log.info("User [{}] Updated Reminder Posts List: {}", username, user.getReminderPosts());
        log.info("Post [{}] Updated Confirmed Users List: {}", postId, post.getConfirmedUsers());

        return user.getReminderPosts();
    }

    // Get all TAGS of the User
    public String[] getTagsOfUser(String token) {
        String username = getUsernameFromToken(token);
        String[] tags = userRepository.findByUsername(username).get().getTags();
        log.info("User [{}] has following tags: [{}]", username, tags);
        return tags;
    }

    // UPDATE the TAGS of User
    public String[] updateTagsOfUser(String[] newTags, String token) {

        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).get();
        String[] currentTags = user.getTags();
        user.setTags(newTags);
        userRepository.save(user);

        log.info("Old tags of User [{}]: {}", username, currentTags);
        log.info("New Tags of user [{}]: {}", username, user.getTags());

        return user.getTags();
    }

    // Add User Display Picture to DB
    public Image addUserDP(MultipartFile file, String token) throws Exception {

        String username = getUsernameFromToken(token);

        if(file == null || file.isEmpty()) {
            log.info("File cannot be null or Empty");
            return null;
        }

        try {
            // Create Image from File
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Long imageId = Long.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));

            // Create New Image
            Image image = new Image(imageId, fileName, file.getContentType(), file.getBytes());

            log.info("Image Id: {}", image.getImageId());
            log.info("Image Filename: {}", image.getFilename());
            log.info("Image Type: {}", image.getMimeType());

            // Save Image to DB
            this.imageRepository.save(image);

            // Save ImageId to User
            User user = this.userRepository.findByUsername(username).get();
            Long oldUserDpId = user.getUserDPId();
            user.setUserDPId(image.getImageId());
            this.userRepository.save(user);

            // Save New ImageId to User - DP ID
            user.setUserDPId(imageId);
            userRepository.save(user);

            // Delete old DP from image repo
            if (imageRepository.findById(oldUserDpId).isPresent()) {
                imageRepository.deleteById(oldUserDpId);
            }

            log.info("Display Picture saved for User [{}]", username);

            return image;
        } catch (Exception e) {
            log.info("Error Uploading Image: {}", e.getMessage());
            return null;
        }

    }

    // GET User DP
    public Image getUserDP(String token) throws Exception {
        String username = getUsernameFromToken(token);
        log.info("DP Requested for USER: [{}]", username);

        try {
            User user = userRepository.findByUsername(username).get();
//            userDP.setFile(ImageUtil.decompressImage(userDP.getFile()));

            if(!checkImageExistInDB(user.getUserDPId())) {
                log.info("User has no DP. Please add one!");
                user.setUserDPId(null);
                userRepository.save(user);
                return null;
            }

            Image userDP = imageRepository.findById(user.getUserDPId()).get();
            log.info("User DP: [{}]", userDP.getFilename());
            return userDP;

        } catch (Exception e) {
            log.info("Error getting User DP. Please try again!! -> {}", e.getMessage());
            return null;
        }
    }

    // Remove user DP
    public int removeUserDP(String token) {
        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).get();
        Long userDpId = user.getUserDPId();
        try {
            if (userDpId != null) {
                user.setUserDPId(null);
                imageRepository.deleteById(userDpId);
                userRepository.save(user);

                log.info("USER [{}] DP Removed successfully", username);
                return 1;
            } else {
                log.info("User [{}] DP does not exist", username);
                return 0;
            }
        } catch (Exception e) {
            log.info("User [{}] DP NOT Removed due to : {}", username, e.getMessage());
            return -1;
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
