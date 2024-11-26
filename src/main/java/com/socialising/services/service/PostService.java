package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.Role;
import com.socialising.services.dto.PostDTO;
import com.socialising.services.exceptionHandler.InvalidDataException;
import com.socialising.services.exceptionHandler.PostUpdateException;
import com.socialising.services.exceptionHandler.TagNotFoundException;
import com.socialising.services.mapper.PostMapper;
import com.socialising.services.model.Post;
import com.socialising.services.model.Tag;
import com.socialising.services.model.User;
import com.socialising.services.repository.ImageRepository;
import com.socialising.services.repository.PostRepository;
import com.socialising.services.repository.TagRepository;
import com.socialising.services.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(com.socialising.services.controller.PostController.class);

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final JwtService jwtService;

    private boolean checkPostExistInDB(Long postId) {
        if(postRepository.findById(postId).isPresent()) {
            log.info("Post [{}] exist in DB", postId);
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
        } catch (Exception e) {
            log.info("Error fetching username from token: {}", e.getMessage());
            return "";
        }
    }

    private boolean checkUserExistInDBWithUsername(String username) {
        if(userRepository.findByUsername(username).isPresent()) {
            log.info("User [{}] exist in DB", username);
            return true;
        }
        log.info("User [{}] does not exists, Please Sign Up!!", username);
        return false;
    }

    private boolean checkUserOwnerOfPostAndRole(String token, String ownerUsername) {
        String username = jwtService.extractUsername(token.substring(7));
        Role userRole = userRepository.findByUsername(username).get().getRole();
        return username.equals(ownerUsername) || userRole.equals(Role.ADMIN);
    }

    // Add a Post to DB
    public PostDTO addPost(PostDTO newPostDTO, String token) {

        try {
            String username = jwtService.extractUsername(token.substring(7));
            log.info("Username: {}", username);

            // Get the user by username or throw an exception
            User ownerUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            // Generate a unique Post ID
            Long postId = Long.valueOf(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));

            // Convert the PostDTO to Post Entity using Mapper
            Post post = PostMapper.dtoToEntity(newPostDTO);
            post.setPostId(postId);
            post.setOwnerUser(ownerUser);
            post.setCreatedTs(Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())));
            post.setConfirmedUsers(new ArrayList<>());
            post.setInterestedUsers(new ArrayList<>());

            // Save the post to the repository
            postRepository.save(post);
            log.info("Post added to db");

            // Return the saved Post entity converted back to DTO
            return PostMapper.entityToDto(post);
        } catch (BadCredentialsException e) {
            log.error("Invalid / Expired token. No Post added to DB");
            throw new BadCredentialsException("Invalid or expired token. Please provide a valid token.");
        } catch (IllegalArgumentException e) {
            log.error("User not found or other input error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding the post: {}", e.getMessage());
            throw new RuntimeException("Unable to add post. Please try again later.");
        }


//        String username = "";
//        try {
//            log.info("Token: {}", token);
//            username = jwtService.extractUsername(token.substring(7));
//            log.info("Username: {}", username);
//        } catch (BadCredentialsException e) {
//            log.info("Invalid / Expired token");
//            log.info("No Post added to DB");
//            return null;
//        }
//
//
//        Long postId = Long.valueOf(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));
//        var post = Post.builder()
//                .postId(postId)
//                .ownerUser(userRepository.findByUsername(username).orElseThrow())
//                .description(newPost.getDescription())
//                .postType(newPost.getPostType())
//                .timeType(newPost.getTimeType())
//                .postStartTs(newPost.getPostStartTs())
//                .postEndTs(newPost.getPostEndTs())
//                .location(newPost.getLocation())
//                .onlyForWomen(newPost.getOnlyForWomen())
//                .tags(newPost.getTags())
//                .createdTs(Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())))
//                .confirmedUsers(new ArrayList<>())
//                .interestedUsers(new ArrayList<>())
//                .build();
//
//        try {
//            this.postRepository.save(post);
//            log.info("Post added to db");
//            return post;
//        } catch (Exception e) {
//            log.info(e.getMessage());
//            return null;
//        }

    }

    // GET All Posts
    public ArrayList<Post> getAllPostsWithoutConversion() {

        log.info("Total number of posts: {}", this.postRepository.count());
        return (ArrayList<Post>) this.postRepository.findAll();
    }

    public ArrayList<PostDTO> getAllPosts() {

        log.info("Total number of posts in DB: {}", this.postRepository.count());
        List<Post> allPosts = this.postRepository.findAll();
        ArrayList<PostDTO> allPostDTOs = new ArrayList<>();

        for(Post post : allPosts) {
            allPostDTOs.add(PostMapper.entityToDto(post));
        }

        return allPostDTOs;
    }

    // Get Posts of Authenticated user
    public List<Post> getUserPosts(String token) {
        String username = jwtService.extractUsername(token.substring(7));
        return postRepository.findByOwnerUserUsername(username);
    }

    // Get Posts by Username
    public List<Post> getPostsByUsername(String username) {
        return postRepository.findByOwnerUserUsername(username);
    }

    //  GET Post by ID
    public Post getPostById(Long id) {

        return checkPostExistInDB(id) ? this.postRepository.findById(id).get() : null;
    }

    private String[] getTagEntities(String[] tags) {
        return Arrays.stream(tags)
                .map(tagName -> {
                    Tag tag = tagRepository.findByTagName(tagName);
                    if (tag == null) {
                        throw new TagNotFoundException("Tag not found: " + tagName);
                    }
                    return tag.getTag();
                })
                .toArray(String[]::new);
    }


    // Update Post
    public PostDTO updatePost(Long postId, String token, PostDTO postDTO) {
        try {
            if (checkPostExistInDB(postId)) {
                Post post = postRepository.findById(postId).get();

                String username = jwtService.extractUsername(token.substring(7));

                // Check if the user updating the details is owner of post
                if (!post.getOwnerUser().getUsername().equals(username)) {
                    log.info("Only Owner of the Post can update its details.");
                    return null;
                }

                // Update fields in the post entity
                if (postDTO.getDescription() != null) post.setDescription(postDTO.getDescription());
                if (postDTO.getPostType() != null) post.setPostType(postDTO.getPostType());
                if (postDTO.getTimeType() != null) post.setTimeType(postDTO.getTimeType());
                if (postDTO.getPostStartTs() != null) post.setPostStartTs(postDTO.getPostStartTs());
                if (postDTO.getPostEndTs() != null) post.setPostEndTs(postDTO.getPostEndTs());
                if (postDTO.getLocation() != null) post.setLocation(postDTO.getLocation());
                post.setOnlyForWomen(postDTO.isOnlyForWomen() ? 'Y' : 'N');

                if (postDTO.getTags() != null && postDTO.getTags().length > 0) {
                    post.setTags(getTagEntities(postDTO.getTags()));
                }

                // Save the updated post
                Post updatedPost = postRepository.save(post);
                log.info("Successfully updated Post with ID: [{}]", postId);
                return PostMapper.entityToDto(updatedPost);
            } else {
                return null;
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided: {}", e.getMessage());
            throw new InvalidDataException("Invalid data provided", e);
        } catch (Exception e) {
            log.error("Error occurred while updating post with ID [{}]: {}", postId, e.getMessage());
            throw new PostUpdateException("Error updating post with ID: " + postId, e);
        }
    }

    // DELETE Post by ID
    public int deletePost(Long postId, String token) {
        if(checkPostExistInDB(postId)) {

            Post post = this.postRepository.findById(postId).get();

            // Check if the user is the owner user of post
            // Only owner user or ADMIN can delete a Post
            String username = jwtService.extractUsername(token.substring(7));
            String ownerUsername = post.getOwnerUser().getUsername();

            if ( !checkUserOwnerOfPostAndRole(token, ownerUsername) ) {
                log.info("User is not authorized to delete the Post");
                return -1;
            }

            // Get the Confirmed Users list of Post
            List<User> confirmedUsers = new ArrayList<>();
            if (post.getConfirmedUsers() != null) {
                confirmedUsers  = post.getConfirmedUsers();
            }

            // Get the Interested Users list of Post
            List<User> interestedUsers = new ArrayList<>();
            if (post.getInterestedUsers() != null) {
                interestedUsers  = post.getInterestedUsers();
            }

            // Delete the post from Confirmed Users Reminder Posts list
            if(!confirmedUsers.isEmpty()) {
                for(User confirmedUser: confirmedUsers) {
                    List<Post> reminderPosts = confirmedUser.getReminderPosts();
                    reminderPosts.remove(post);
                    confirmedUser.setReminderPosts(reminderPosts);
                    userRepository.save(confirmedUser);
                    log.info("Post [{}] removed from User [{}'s] Reminder posts list", postId, confirmedUser.getUsername());
                }
            }

            // Delete the post from Interested Users Requested Posts list
            if(!interestedUsers.isEmpty()) {
                for(User interestedUser: interestedUsers) {
                    List<Post> requestedPosts = interestedUser.getRequestedPosts();
                    requestedPosts.remove(post);
                    interestedUser.setRequestedPosts(requestedPosts);
                    userRepository.save(interestedUser);
                    log.info("Post [{}] removed from User [{}'s] Requested Posts list", postId, interestedUser.getUsername());
                }
            }

            // Delete Post from DB
            postRepository.deleteById(postId);
            log.info("Post with Post ID: {} deleted from DB", postId);

            return 1;
        }
        return -1;
    }

    // Interested User Request for a Post
    public int postUserRequest(Long postId, String token) {
        if(checkPostExistInDB(postId)) {

            // Get the interested User
            String username = jwtService.extractUsername(token.substring(7));
            User user = userRepository.findByUsername(username).get();

            // Get the list of current interested users
            Post post = postRepository.findById(postId).get();
            List<User> interestedUsers = new ArrayList<>();
            if (post.getInterestedUsers() != null) {
                interestedUsers = post.getInterestedUsers() ;
            }
            log.info("Interested Users before: {}", interestedUsers);

            // Check if user is the owner user of Post
            if (user.getUsername().equals(post.getOwnerUser().getUsername())) {
                log.info("User [{}] is the owner of the Post", user.getUsername());
                return -1;
            }

            // check if User is already in the list of interested users list
            if (!interestedUsers.isEmpty() && interestedUsers.contains(user)) {
                log.info("User [{}] already exists in Interested Users list for Post {}", username, postId);
                return 0;
            } else {

                List<Post> requestedPosts = new ArrayList<>();
                if (user.getRequestedPosts() != null) {
                    requestedPosts = user.getRequestedPosts();
                }
                requestedPosts.add(post);
                user.setRequestedPosts(requestedPosts);
                userRepository.save(user);

                interestedUsers.add(user);
                post.setInterestedUsers(interestedUsers);
                postRepository.save(post);
                log.info("User [{}] added to Interested Users List for Post [{}]", user.getUsername(), postId);

                return 1;
            }
        }
        return -1;
    }

    // Get All Interested Users for a Post
    // This method checks if a post exists in the database, retrieves the list of interested users, logs the appropriate message, and returns the usernames of the interested users.
    public List<String> getInterestedUsers(Long postId, String token) {
        if(checkPostExistInDB(postId)) {

            Post post = postRepository.findById(postId).get();

            if (!checkUserOwnerOfPostAndRole(token, post.getOwnerUser().getUsername())) {
                log.info("User is not authorized to get the interested Users");
                return null;
            }

            List<User> interestedUsers = post.getInterestedUsers();

            if(interestedUsers.isEmpty()) {
                log.info("No Interested Users for Post {}", postId);
            } else {
                log.info("Interested Users for Post {} are {}", postId, interestedUsers.size());
            }

            return interestedUsers.stream().map(User::getUsername).collect(Collectors.toList());
        }
        return null;
    }

    // ACCEPT Interested User for a post
    public int acceptInterestedUser(Long postId, String username, String token) {

        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        if(!checkUserExistInDBWithUsername(username)) {
            return -1;
        }

        Post post = this.postRepository.findById(postId).get();
        User user = userRepository.findByUsername(username).get();
        List<User> interestedUsers = post.getInterestedUsers();
        List<Post> requestedPosts = user.getRequestedPosts();

        // Check if the User is Authorized to Accept the User
        // Only ADMIN or Post's Owner User is allowed
        if (!checkUserOwnerOfPostAndRole(token, post.getOwnerUser().getUsername())) {
            log.info("User [{}] is NOT Authorized to accept the User Request", jwtService.extractUsername(token.substring(7)));
            return -1;
        }

        // Check if user already exists
        if(!interestedUsers.contains(user)) {
            log.info("User [{}] does not exists in Post interested Users list. Please raise request for the post", user.getUsername());
            return -1;
        }
        else {
            // delete user from interestedUsers array
            interestedUsers.remove(user);
            log.info("User [{}] deleted from the Post [{}] interested Users list", user.getUsername(), postId);

            // delete post from user's requested Post list
            requestedPosts.remove(post);
            log.info("Post [{}] deleted from the user [{}] requested Posts list", postId, user.getUsername());
        }

        List<User> confirmedUsers = post.getConfirmedUsers();

        // Check if user already exists in Post's Confirmed User list
        if(confirmedUsers.contains(user)) {
            log.info("User [{}] already exists in confirmed users list for post [{}]. Check again", user.getUsername(), postId);
        }
        else {
            // Add user to confirmedUsers array
            confirmedUsers.add(user);
            log.info("User [{}] added to Post {} confirmed Users List. See you soon fella!!", user.getUsername(), postId);
        }

        List<Post> reminderPosts = user.getReminderPosts();

        if (reminderPosts == null) {
            reminderPosts = new ArrayList<>();
        }

        if(reminderPosts.contains(post)) {
            log.info("Post [{}] already exists in User [{}] reminder bucket list of posts!!", postId, user.getUsername());
        }
        else {
            // Add post to user's reminder posts list
            reminderPosts.add(post);
            log.info("Post [{}] added to User [{}] reminder Posts list", postId, user.getUsername());
            log.info("Number of Requested Posts for User [{}] are: [{}]", username, user.getRequestedPosts().size());
        }

        post.setInterestedUsers(interestedUsers);
        user.setRequestedPosts(requestedPosts);
        post.setConfirmedUsers(confirmedUsers);
        user.setReminderPosts(reminderPosts);

        postRepository.save(post);
        userRepository.save(user);

        log.info("Number of Reminder Posts for User [{}] are: [{}]", username, user.getReminderPosts().size());
        log.info("Number of Interested Users for Post [{}] are: [{}]", postId, post.getInterestedUsers().size());
        log.info("Number of Confirmed Users for Post [{}] are: [{}]", postId, post.getConfirmedUsers().size());
        log.info("For Post {}, added User [{}] to Confirmed Users list, removed from Interested Users List",
                postId, username);
        return 1;
    }

    // Reject Interested user of the post
    public int rejectInterestedUser(Long postId, String username, String token) {
        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        if(!checkUserExistInDBWithUsername(username)) {
            return -1;
        }

        Post post = postRepository.findById(postId).get();
        User user = userRepository.findByUsername(username).get();

        // Check if the User is Authorized to Reject the User
        // Only ADMIN or Post's Owner User is allowed
        if (!checkUserOwnerOfPostAndRole(token, post.getOwnerUser().getUsername())) {
            log.info("User [{}] is NOT Authorized to reject the User Request", jwtService.extractUsername(token.substring(7)));
            return -1;
        }

        List<User> interestedUsers = post.getInterestedUsers();
        List<Post> requestedPosts = user.getRequestedPosts();

        if (interestedUsers.remove(user) && requestedPosts.remove(post)) {
            log.info("User [{}] removed from Interested Users list", user.getUsername());

            post.setInterestedUsers(interestedUsers);
            user.setRequestedPosts(requestedPosts);

            postRepository.save(post);
            userRepository.save(user);

            return 1;
        }
        log.info("User [{}] NOT rejected", username);
        return -1;
    }

    // GET All the confirmed users
    public List<String> getConfirmedUsers(Long postId) {
        if(checkPostExistInDB(postId)) {
            List<User> confirmedUsers = this.postRepository.findById(postId).get().getConfirmedUsers();

            if(confirmedUsers.isEmpty()) {
                log.info("No Confirmed Users for Post {}", postId);
            } else {
                log.info("Confirmed Users for Post {} are {}", postId, confirmedUsers.size());
            }

            return confirmedUsers.stream().map(User::getUsername).collect(Collectors.toList());
        }
        return null;
    }

    // DELETE a Confirmed User from Post
    public int deleteConfirmedUser(Long postId, String username, String token) {
        if (!checkPostExistInDB(postId)) {
            return -1;
        }

        Post post = postRepository.findById(postId).get();
        if (!checkUserExistInDBWithUsername(username)) {
            log.info("User [{}] should not be in Confirmed Users list of Post [{}]. Check and Delete!!", username, postId);
            return -1;
        }

        // Check if the User is Authorized to Delete the Confirmed User
        // Only ADMIN or Post's Owner User is allowed
        if (!checkUserOwnerOfPostAndRole(token, post.getOwnerUser().getUsername())) {
            log.info("User [{}] is NOT Authorized to delete the User Request", jwtService.extractUsername(token.substring(7)));
            return -1;
        }

        User user = userRepository.findByUsername(username).get();
        List<User> confirmedUsers = post.getConfirmedUsers();

        if (!confirmedUsers.contains(user)) {
            log.info("User [{}] does not exist in Confirmed Users List of Post [{}]", username, postId);
            return 0;
        }

        confirmedUsers.remove(user);
        post.setConfirmedUsers(confirmedUsers);
        postRepository.save(post);
        log.info("Post [{}] removed from User [{}] Reminder Posts list", postId, username);

        // Delete Post from User's Reminder Posts List
        List<Post> reminderPosts = user.getReminderPosts();
        reminderPosts.remove(post);
        user.setReminderPosts(reminderPosts);
        userRepository.save(user);
        log.info("User [{}] removed from Confirmed Users list [{}]", username, confirmedUsers);

        return 1;
    }

    // Like a Post by User
    public int likeAPost(Long postId, String token) {
        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        String username = jwtService.extractUsername(token.substring(7));

        Post post = postRepository.findById(postId).get();
        String[] likes = post.getLikes() != null ? post.getLikes() : new String[]{};
        if(ArrayUtils.contains(likes, username)) {
            log.info("User [{}] has already liked the post [{}]", username, postId);
            return 0;
        }

        // Add user to likes list of the post
        likes = ArrayUtils.add(likes, username);
        post.setLikes(likes);
        postRepository.save(post);

        log.info("User [{}] has liked the post [{}], and added to LIKES list of the Post", username, postId);
        return 1;
    }

    // GET All Likes on post
    public String[] getAllLikesOnPost(Long postId) {
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        Post post = postRepository.findById(postId).get();
        String[] likes = post.getLikes();

        if(ArrayUtils.isEmpty(likes)) {
            log.info("No LIKES given to the Post [{}]", postId);
        }
        else {
            log.info("No. of Likes given to the Post [{}] are: [{}]", postId, likes.length);
        }

        return likes != null ? likes : new String[]{};
    }

    // Remove a Like on post by user
    public int removeAlikeOnPost(Long postId, String token) {
        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        String username = jwtService.extractUsername(token.substring(7));

        Post post = postRepository.findById(postId).get();
        String[] likes = post.getLikes() != null ? post.getLikes() : new String[]{};
        if(!ArrayUtils.contains(likes, username)) {
            log.info("User [{}] has NOT liked the post [{}]", username, postId);
            return 0;
        }

        // Remove user from the LIKES list of the post
        likes = ArrayUtils.removeElement(likes, username);
        post.setLikes(likes);
        postRepository.save(post);

        log.info("User [{}] has dis-liked the post [{}], and removed from LIKES list of the Post", username, postId);
        return 1;
    }

    // ADD Hashtags to Current Hashtags Post
    public String[] addHashtags(Long postId, String[] newHashtags) {
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        Post post = postRepository.findById(postId).get();

        // Add Hashtags to current Hashtags
        String[] hashtags = post.getHashtags() != null ? post.getHashtags() : new String[]{};
        hashtags = ArrayUtils.addAll(hashtags, newHashtags);
        post.setHashtags(hashtags);
        postRepository.save(post);

        log.info("New Hashtags Added to post [{}] : {}", postId, hashtags);
        return post.getHashtags();
    }

    // GET Hashtags of Post
    public String[] getHashtagsOfPost(Long postId) {
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        String[] hashtagsOfPost = postRepository.findById(postId).get().getHashtags();

        return hashtagsOfPost != null ? hashtagsOfPost : new String[]{};
    }

    // UPDATE Hashtags to Current Hashtags Post
    public String[] updateHashtags(Long postId, String[] newHashtags) {
        if(!checkPostExistInDB(postId)) {
            return null;
        }

        Post post = postRepository.findById(postId).get();

        // Replace Hashtags
        String[] OldHashtags = post.getHashtags();
        post.setHashtags(newHashtags);
        postRepository.save(post);

        log.info("Old Hashtags: {} of Post {} are removed", OldHashtags, postId);
        log.info("New Hashtags {} Added to post {}", newHashtags, postId);
        return post.getHashtags();
    }

    // DELETE Hashtag from Post
    public int deleteHashtagsOfPost(Long postId, String hashtag) {
        if(!checkPostExistInDB(postId)) {
            return -1;
        }

        Post post = postRepository.findById(postId).get();

        // Delete Hashtag
        String[] hashtags = post.getHashtags();

        if (!ArrayUtils.contains(hashtags, hashtag)) {
            log.info("Hashtag [{}] does not exist for Post [{}]", hashtag, postId);
            return 0;
        }

        hashtags = ArrayUtils.removeElement(hashtags, hashtag);
        post.setHashtags(hashtags);
        postRepository.save(post);

        log.info("Hashtag [{}] deleted from Post {}", hashtag, postId);
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
