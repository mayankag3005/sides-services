package com.socialising.services.service;

import com.socialising.services.dto.PostDTO;
import com.socialising.services.mapper.PostMapper;
import org.apache.commons.lang3.ArrayUtils;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.Role;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.repository.ImageRepository;
import com.socialising.services.repository.PostRepository;
import com.socialising.services.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private Post secondTestPost;
    private Long postId;
    private Long secondPostId;
    private String adminUsername;
    private String ownerUsername;
    private String otherUsername;
    private String secondOtherUsername;
    private User adminUser;
    private User ownerUser;
    private User otherUser;
    private User secondOtherUser;
    private PostDTO testPostDTO;

//    private MemoryAppender memoryAppender;

    // In-memory appender to capture log events
//    public class MemoryAppender extends ListAppender<ILoggingEvent> {
//        public void reset() {
//            this.list.clear();
//        }
//
//        public boolean contains(String string, Level level) {
//            return this.list.stream()
//                    .anyMatch(event -> event.toString().contains(string)
//                            && event.getLevel().equals(level));
//        }
//
//        public int countEventsForLogger(String loggerName) {
//            return (int) this.list.stream()
//                    .filter(event -> event.getLoggerName().contains(loggerName))
//                    .count();
//        }
//
//        public List<ILoggingEvent> search(String string) {
//            return this.list.stream()
//                    .filter(event -> event.toString().contains(string))
//                    .collect(Collectors.toList());
//        }
//
//        public List<ILoggingEvent> search(String string, Level level) {
//            return this.list.stream()
//                    .filter(event -> event.toString().contains(string)
//                            && event.getLevel().equals(level))
//                    .collect(Collectors.toList());
//        }
//
//        public int getSize() {
//            return this.list.size();
//        }
//
//        public List<ILoggingEvent> getLoggedEvents() {
//            return Collections.unmodifiableList(this.list);
//        }
//    }

    @BeforeEach
    void setUp() {

        postId = 1L;
        secondPostId = 6L;

        adminUsername = "adminUser";
        ownerUsername = "ownerUser";
        otherUsername = "otherUser";
        secondOtherUsername = "secondOtherUser";

        testPostDTO = PostDTO.builder()
                .description("This is test post")
                .postType("general")
                .timeType("later")
                .postStartTs("2024-07-13")
                .postEndTs("2024-08-15")
                .location("Amity")
                .onlyForWomen(false)
                .build();

        ownerUser = User.builder()
                .userId(2L)
                .username(ownerUsername)
                .email("owner@example.com")
                .role(Role.USER)
                .build();

        testPost = Post.builder()
                .postId(postId)
                .ownerUser(ownerUser)
                .description("This is test post")
                .postType("general")
                .timeType("later")
                .postStartTs("2024-07-13")
                .postEndTs("2024-08-15")
                .location("Amity")
                .onlyForWomen('N')
                .confirmedUsers(new ArrayList<>())
                .interestedUsers(new ArrayList<>())
                .build();

        secondTestPost = Post.builder()
                .postId(secondPostId)
                .ownerUser(ownerUser)
                .description("This is second test post")
                .postType("general")
                .timeType("later")
                .postStartTs("2024-06-21")
                .postEndTs("2024-06-22")
                .location("Srinagar")
                .onlyForWomen('Y')
                .confirmedUsers(new ArrayList<>())
                .interestedUsers(new ArrayList<>())
                .build();

        adminUser = User.builder()
                .userId(5L)
                .username(adminUsername)
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();

        otherUser = User.builder()
                .userId(3L)
                .username(otherUsername)
                .email("other@example.com")
                .role(Role.USER)
                .build();

        secondOtherUser = User.builder()
                .userId(7L)
                .username(secondOtherUsername)
                .email("other@example.com")
                .role(Role.USER)
                .build();

//        Logger logger = (Logger) LoggerFactory.getLogger(PostService.class);
//        memoryAppender = new MemoryAppender();
//        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
//        logger.setLevel(Level.INFO);
//        logger.addAppender(memoryAppender);
//        memoryAppender.start();
    }

    // addPost

    @Test
    public void should_add_post() throws Exception {

        // Mock JWT token
        String mockJwtToken = "mock.jwt.token";

        // Mock username and user data
        when(jwtService.extractUsername(anyString())).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // Mock saving of post
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        PostDTO addedPostDTO = postService.addPost(testPostDTO, "Bearer " + mockJwtToken);

        // Then
        assertNotNull(addedPostDTO);
        assertEquals(testPost.getDescription(), addedPostDTO.getDescription());

        // Verify interactions
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(userRepository, times(1)).findByUsername(ownerUsername);
        verify(postRepository, times(1)).save(any());
    }

    @Test
    public void should_not_add_post_when_invalid_token_is_passed() throws Exception {

        // Mock JWT token
        String mockJwtToken = "Bearer invalid.jwt.token";

        // Mock username and user data
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenThrow(new BadCredentialsException("Invalid / Expired token"));

        // When
        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            postService.addPost(testPostDTO, mockJwtToken);
        });

        // Then
        assertEquals("Invalid or expired token. Please provide a valid token.", exception.getMessage());

        // Verify interactions
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, never()).findByUsername(ownerUsername);
        verify(postRepository, never()).save(any());
    }

    @Test
    public void should_not_add_post_when_user_not_found() {
        // Mock JWT token
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock username extraction and user retrieval
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.addPost(testPostDTO, mockJwtToken);
        });

        // Then
        assertEquals("User not found: testUser", exception.getMessage());
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(postRepository, never()).save(any());
    }

    @Test
    public void should_not_add_post_when_save_fails() throws Exception {

        // Mock JWT token
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock username and user data
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));
        when(postRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.addPost(testPostDTO, mockJwtToken);
        });

        // Then
        assertEquals("Unable to add post. Please try again later.", exception.getMessage());

        // Verify interactions
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, times(1)).findByUsername(ownerUsername);
        verify(postRepository, times(1)).save(any());
    }

    // getAllPosts

    @Test
    public void should_get_all_posts() {
        // Given
        List<Post> mockPosts = new ArrayList<>();
        mockPosts.add(Post.builder()
                .description("This is first test post")
                .postType("general")
                .timeType("later")
                .postStartTs("2024-06-11")
                .postEndTs("2024-06-15")
                .location("Kanpur")
                .onlyForWomen('Y')
                .build());

        mockPosts.add(Post.builder()
                .description("This is second test post")
                .postType("event")
                .timeType("now")
                .postStartTs("2024-07-13")
                .postEndTs("2024-08-15")
                .location("Amity")
                .onlyForWomen('N')
                .build());

        // Mock
        when(postRepository.findAll()).thenReturn(mockPosts);
        when(postRepository.count()).thenReturn((long) mockPosts.size());

        // When
        ArrayList<Post> allPosts = postService.getAllPosts();

        // Then
        assertNotNull(allPosts);
        assertEquals(allPosts.size(), 2);
        assertEquals(allPosts.get(0).getDescription(), "This is first test post");

        verify(postRepository).count();
        verify(postRepository).findAll();
    }

    // getPostById

    @Test
    public void should_not_get_post_when_not_exist() {
        // given
        Long postId = 4L;

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        Post responsePost = postService.getPostById(postId);

        // Then
        assertNull(responsePost);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void should_get_post_when_passed_id() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        Post responsePost = postService.getPostById(postId);

        // Then
        assertNotNull(responsePost);
        assertEquals(postId, responsePost.getPostId());
        verify(postRepository, times(2)).findById(postId);
    }

    // deletePost

    @Test
    public void should_not_delete_post_if_not_exist() {
        // Given
        Long postId = 4L;
        String mockJwtToken = "mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        int result = postService.deletePost(postId, mockJwtToken);

        // Then
        assertEquals(-1, result);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).deleteById(postId);
    }

    @Test
    public void should_delete_post_when_user_is_authorized() {
        // Mock JWT token
        String mockJwtToken = "Bearer valid.jwt.token";

        // Mock username and user data
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // When
        int result = postService.deletePost(postId, mockJwtToken);

        // Then
        assertEquals(1, result);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository).deleteById(postId);
//        assertEquals(log.info()).info("Post with Post ID: {} deleted from DB", postId);
    }

    @Test
    public void should_delete_post_when_user_is_admin() {
        // Mock JWT token
        String mockJwtToken = "Bearer admin.jwt.token";

        // Mock username and user data
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(adminUsername);
        when(userRepository.findByUsername(adminUsername)).thenReturn(Optional.of(adminUser));

        // When
        int result = postService.deletePost(postId, mockJwtToken);

        // Then
        assertEquals(1, result);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository).deleteById(postId);
//        assertEquals(log.info()).info("Post with Post ID: {} deleted from DB", postId);
    }

    @Test
    public void should_not_delete_post_when_user_is_not_authorized() {
        // Given
        String token = "Bearer invalid.token.here";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));

        // When
        int result = postService.deletePost(postId, token);

        // Then
        assertEquals(-1, result);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository, never()).deleteById(postId);
    }

    // Test for deleting post from confirmed users reminder posts list & interested users requested posts list, when a post is deleted
    @Test
    public void should_delete_post_and_remove_post_from_confirmed_users_reminder_posts_list_and_interested_users_requested_posts_list() {
        // Given
        String token = "Bearer mock.jwt.token";

        List<User> interestedUsers = new ArrayList<>();
        otherUser.setRequestedPosts(new ArrayList<>(Collections.singletonList(testPost)));
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        List<User> confirmedUsers = new ArrayList<>();
        secondOtherUser.setReminderPosts(new ArrayList<>(Collections.singletonList(testPost)));
        confirmedUsers.add(secondOtherUser);
        testPost.setConfirmedUsers(confirmedUsers);

        // Mock
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // When
        int result = postService.deletePost(postId, token);

        // Then
        assertEquals(1, result);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository).deleteById(postId);
        verify(userRepository).save(otherUser);
        verify(userRepository).save(secondOtherUser);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, secondOtherUser.getReminderPosts().size());
    }


    // postUserRequest

    @Test
    public void should_add_user_to_post_interested_user_list_when_user_sends_invite_request() {
        // Given
        String token = "Bearer mock.jwt.token";

        List<Post> expectedRequestedPosts = new ArrayList<>();
        expectedRequestedPosts.add(testPost);

        List<User> expectedInterestedUsers = new ArrayList<>();
        expectedInterestedUsers.add(otherUser);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int res = postService.postUserRequest(postId, token);

        // Then
        assertEquals(1, res);
        assertEquals(1, otherUser.getRequestedPosts().size());
        assertEquals(1, testPost.getInterestedUsers().size());
        assertEquals(expectedRequestedPosts, otherUser.getRequestedPosts());
        assertEquals(expectedInterestedUsers, testPost.getInterestedUsers());
        verify(userRepository, times(1)).save(otherUser);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_not_add_user_to_post_interested_user_list_when_user_is_already_present_in_list() {
        // Given
        String token = "Bearer mock.jwt.token";

        List<Post> requestedPosts = new ArrayList<>();
        requestedPosts.add(testPost);
        otherUser.setRequestedPosts(requestedPosts);

        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));

        // When
        int res = postService.postUserRequest(postId, token);

        // Then
        assertEquals(0, res);
        assertEquals(1, otherUser.getRequestedPosts().size());
        assertEquals(1, testPost.getInterestedUsers().size());
        assertEquals(requestedPosts.size(), otherUser.getRequestedPosts().size());
        assertEquals(interestedUsers.size(), testPost.getInterestedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_add_user_to_post_interested_user_list_when_user_is_owner_of_post() {
        // Given
        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // When
        int res = postService.postUserRequest(postId, token);

        // Then
        assertEquals(-1, res);
//        assertTrue(memoryAppender.contains("User [" + ownerUsername + "] is the owner of the Post", Level.INFO));
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_add_user_to_interested_user_list_when_post_does_not_exist() {
        // Given
        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        int res = postService.postUserRequest(postId, token);

        // Then
        assertEquals(-1, res);
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_add_user_to_interested_user_list_with_existing_interested_user_of_post() {
        // Given
        String token = "Bearer mock.jwt.token";

        List<User> existingInterestedUsers = new ArrayList<>();
        existingInterestedUsers.add(secondOtherUser);
        testPost.setInterestedUsers(existingInterestedUsers);

        List<Post> expectedRequestedPosts = new ArrayList<>();
        expectedRequestedPosts.add(testPost);

        List<User> expectedInterestedUsers = new ArrayList<>();
        expectedInterestedUsers.add(secondOtherUser);
        expectedInterestedUsers.add(otherUser);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int res = postService.postUserRequest(postId, token);

        // Then
        assertEquals(1, res);
        assertEquals(1, otherUser.getRequestedPosts().size());
        assertEquals(2, testPost.getInterestedUsers().size());
        assertEquals(expectedRequestedPosts, otherUser.getRequestedPosts());
        assertEquals(expectedInterestedUsers, testPost.getInterestedUsers());
        verify(userRepository, times(1)).save(otherUser);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_add_user_to_interested_user_list_with_existing_requested_post_of_user() {
        // Given
        String token = "Bearer mock.jwt.token";

        List<Post> existingRequestedPost = new ArrayList<>();
        existingRequestedPost.add(secondTestPost);
        otherUser.setRequestedPosts(existingRequestedPost);

        List<Post> expectedRequestedPosts = new ArrayList<>();
        expectedRequestedPosts.add(secondTestPost);
        expectedRequestedPosts.add(testPost);

        List<User> expectedInterestedUsers = new ArrayList<>();
        expectedInterestedUsers.add(otherUser);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int res = postService.postUserRequest(postId, token);

        // Then
        assertEquals(1, res);
        assertEquals(2, otherUser.getRequestedPosts().size());
        assertEquals(1, testPost.getInterestedUsers().size());
        assertEquals(expectedRequestedPosts, otherUser.getRequestedPosts());
        assertEquals(expectedInterestedUsers, testPost.getInterestedUsers());
        verify(userRepository, times(1)).save(otherUser);
        verify(postRepository, times(1)).save(testPost);
    }

    // getInterestedUsers

    @Test
    public void should_get_interested_users_when_users_exist() {
        // Given
        String token = "Bearer mock.jwt.token";
        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // When
        List<String> responseInterestedUsers = postService.getInterestedUsers(postId, token);

        // Then
        assertNotNull(responseInterestedUsers);
        assertEquals(interestedUsers.size(), responseInterestedUsers.size());
        assertEquals(interestedUsers.get(0).getUsername(), responseInterestedUsers.get(0));
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_get_zero_interested_users_when_users_does_not_exist() {
        // Given
        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // When
        List<String> responseInterestedUsers = postService.getInterestedUsers(postId, token);

        // Then
        assertNotNull(responseInterestedUsers);
        assertEquals(0, responseInterestedUsers.size());
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_get_null_for_interested_users_when_post_does_not_exist() {
        // Given
        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        List<String> responseInterestedUsers = postService.getInterestedUsers(postId, token);

        // Then
        assertNull(responseInterestedUsers);
        verify(postRepository, times(1)).findById(postId);
    }

    // acceptInterestedUser

    @Test
    public void should_accept_user_when_request_is_present_and_user_is_not_already_a_confirmed_user_of_post() {
        // Given
        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        List<Post> requestedPosts = new ArrayList<>();
        requestedPosts.add(testPost);
        otherUser.setRequestedPosts(requestedPosts);

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.acceptInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        assertEquals(1, otherUser.getReminderPosts().size());
        assertEquals(1, testPost.getConfirmedUsers().size());
    }

    @Test
    public void should_accept_user_when_request_is_present_but_do_not_update_reminder_post_when_post_is_already_in_reminder_post_list_of_user() {
        // Given
        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        List<Post> requestedPosts = new ArrayList<>();
        requestedPosts.add(testPost);
        otherUser.setRequestedPosts(requestedPosts);

        List<Post> reminderPosts = new ArrayList<>();
        reminderPosts.add(testPost);
        otherUser.setReminderPosts(reminderPosts);

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.acceptInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        assertEquals(1, otherUser.getReminderPosts().size());
        assertEquals(1, testPost.getConfirmedUsers().size());
    }

    @Test
    public void should_accept_user_when_request_is_present_but_do_not_update_confirmed_user_when_user_is_already_in_confirmed_user_list_of_post() {
        // Given
        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        List<User> confirmedUsers = new ArrayList<>();
        confirmedUsers.add(otherUser);
        testPost.setConfirmedUsers(confirmedUsers);

        List<Post> requestedPosts = new ArrayList<>();
        requestedPosts.add(testPost);
        otherUser.setRequestedPosts(requestedPosts);

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.acceptInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        assertEquals(1, otherUser.getReminderPosts().size());
        assertEquals(1, testPost.getConfirmedUsers().size());
    }

    @Test
    public void should_not_accept_user_when_request_is_not_present() {
        // Given
        testPost.setInterestedUsers(new ArrayList<>());
        testPost.setConfirmedUsers(new ArrayList<>());
        otherUser.setRequestedPosts(new ArrayList<>());
        otherUser.setReminderPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.acceptInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        assertEquals(0, otherUser.getReminderPosts().size());
        assertEquals(0, testPost.getConfirmedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_accept_user_when_request_is_present_but_user_is_not_authorized() {
        // Given
        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        List<Post> requestedPosts = new ArrayList<>();
        requestedPosts.add(testPost);
        otherUser.setRequestedPosts(requestedPosts);

        testPost.setConfirmedUsers(new ArrayList<>());
        otherUser.setReminderPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);

        // When
        int result = postService.acceptInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(1, otherUser.getRequestedPosts().size());
        assertEquals(1, testPost.getInterestedUsers().size());
        assertEquals(0, otherUser.getReminderPosts().size());
        assertEquals(0, testPost.getConfirmedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_accept_user_when_post_does_not_exist() {
        // Given
        testPost.setInterestedUsers(new ArrayList<>());
        otherUser.setRequestedPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        int result = postService.acceptInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_accept_user_when_user_does_not_exist() {
        // Given
        testPost.setInterestedUsers(new ArrayList<>());
        otherUser.setRequestedPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.empty());

        // When
        int result = postService.acceptInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    // rejectInterestedUser

    @Test
    public void should_reject_user_when_request_is_present_and_user_is_not_already_a_confirmed_user_of_post() {
        // Given
        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        List<Post> requestedPosts = new ArrayList<>();
        requestedPosts.add(testPost);
        otherUser.setRequestedPosts(requestedPosts);

        testPost.setConfirmedUsers(new ArrayList<>());
        otherUser.setReminderPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.rejectInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        assertEquals(0, otherUser.getReminderPosts().size());
        assertEquals(0, testPost.getConfirmedUsers().size());
        verify(userRepository, times(1)).save(otherUser);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_not_reject_user_when_request_is_not_present() {
        // Given
        testPost.setInterestedUsers(new ArrayList<>());
        otherUser.setRequestedPosts(new ArrayList<>());
        testPost.setConfirmedUsers(new ArrayList<>());
        otherUser.setReminderPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // When
        int result = postService.rejectInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        assertEquals(0, otherUser.getReminderPosts().size());
        assertEquals(0, testPost.getConfirmedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_reject_user_when_request_is_present_but_user_is_not_authorized() {
        // Given
        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        List<Post> requestedPosts = new ArrayList<>();
        requestedPosts.add(testPost);
        otherUser.setRequestedPosts(requestedPosts);

        testPost.setConfirmedUsers(new ArrayList<>());
        otherUser.setReminderPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);

        // When
        int result = postService.rejectInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(1, otherUser.getRequestedPosts().size());
        assertEquals(1, testPost.getInterestedUsers().size());
        assertEquals(0, otherUser.getReminderPosts().size());
        assertEquals(0, testPost.getConfirmedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_reject_user_when_post_does_not_exist() {
        // Given
        testPost.setInterestedUsers(new ArrayList<>());
        otherUser.setRequestedPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        int result = postService.rejectInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_reject_user_when_user_does_not_exist() {
        // Given
        testPost.setInterestedUsers(new ArrayList<>());
        otherUser.setRequestedPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.empty());

        // When
        int result = postService.rejectInterestedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(0, otherUser.getRequestedPosts().size());
        assertEquals(0, testPost.getInterestedUsers().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    // getConfirmedUsers

    @Test
    public void should_get_confirmed_users_when_users_exist() {
        // Given
        List<User> confirmedUsers = new ArrayList<>();
        confirmedUsers.add(otherUser);
        testPost.setConfirmedUsers(confirmedUsers);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        List<String> responseConfirmedUsers = postService.getConfirmedUsers(postId);

        // Then
        assertNotNull(responseConfirmedUsers);
        assertEquals(confirmedUsers.size(), responseConfirmedUsers.size());
        assertEquals(confirmedUsers.get(0).getUsername(), responseConfirmedUsers.get(0));
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_get_zero_confirmed_users_when_users_does_not_exist() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        List<String> responseConfirmedUsers = postService.getConfirmedUsers(postId);

        // Then
        assertNotNull(responseConfirmedUsers);
        assertEquals(0, responseConfirmedUsers.size());
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_get_null_for_confirmed_users_when_post_does_not_exist() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        List<String> responseConfirmedUsers = postService.getConfirmedUsers(postId);

        // Then
        assertNull(responseConfirmedUsers);
        verify(postRepository, times(1)).findById(postId);
    }

    // deleteConfirmedUser

    @Test
    public void should_delete_confirmed_user_when_exists() {
        // Given
        List<User> confirmedUsers = new ArrayList<>();
        confirmedUsers.add(otherUser);
        testPost.setConfirmedUsers(confirmedUsers);

        List<Post> reminderPosts = new ArrayList<>();
        reminderPosts.add(testPost);
        otherUser.setReminderPosts(reminderPosts);

        String token = "Bearer mock.jwt.token";

        // Mock
         when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
         when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
         when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
         when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));
         when(userRepository.save(otherUser)).thenReturn(otherUser);
         when(postRepository.save(testPost)).thenReturn(testPost);

         // When
         int result = postService.deleteConfirmedUser(postId, otherUsername, token);

         // Then
         assertEquals(1, result);
         assertEquals(0, testPost.getConfirmedUsers().size());
         assertEquals(0, otherUser.getReminderPosts().size());
         verify(userRepository, times(1)).save(otherUser);
         verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_not_delete_user_when_user_does_not_exist_in_confirmed_user_list() {
        // Given
        testPost.setConfirmedUsers(new ArrayList<>());

        otherUser.setReminderPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // When
        int result = postService.deleteConfirmedUser(postId, otherUsername, token);

        // Then
        assertEquals(0, result);
        assertEquals(0, testPost.getConfirmedUsers().size());
        assertEquals(0, otherUser.getReminderPosts().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_delete_user_when_user_is_not_authorized_to_delete_other_user() {
        // Given
        List<User> confirmedUsers = new ArrayList<>();
        confirmedUsers.add(otherUser);
        testPost.setConfirmedUsers(confirmedUsers);

        List<Post> reminderPosts = new ArrayList<>();
        reminderPosts.add(testPost);
        otherUser.setReminderPosts(reminderPosts);

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.of(otherUser));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);

        // When
        int result = postService.deleteConfirmedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(1, testPost.getConfirmedUsers().size());
        assertEquals(1, otherUser.getReminderPosts().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_delete_user_when_user_is_not_in_database() {
        // Given
        testPost.setConfirmedUsers(new ArrayList<>());
        otherUser.setReminderPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername(otherUsername)).thenReturn(Optional.empty());

        // When
        int result = postService.deleteConfirmedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(0, testPost.getConfirmedUsers().size());
        assertEquals(0, otherUser.getReminderPosts().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_delete_user_when_post_does_not_exist() {
        // Given
        testPost.setConfirmedUsers(new ArrayList<>());
        otherUser.setReminderPosts(new ArrayList<>());

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        int result = postService.deleteConfirmedUser(postId, otherUsername, token);

        // Then
        assertEquals(-1, result);
        assertEquals(0, testPost.getConfirmedUsers().size());
        assertEquals(0, otherUser.getReminderPosts().size());
        verify(userRepository, never()).save(otherUser);
        verify(postRepository, never()).save(testPost);
    }

    // likeAPost

    @Test
    public void should_like_post() {
        // Given
        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.likeAPost(postId, token);

        // Then
        assertEquals(1, result);
        assertEquals(1, testPost.getLikes().length);
        assertEquals(otherUsername, testPost.getLikes()[0]);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_not_like_post_when_already_liked_by_same_user() {
        // Given
        String[] likes = {"otherUser"};
        testPost.setLikes(likes);

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);

        // When
        int result = postService.likeAPost(postId, token);

        // Then
        assertEquals(0, result);
        assertEquals(1, testPost.getLikes().length);
        assertEquals(otherUsername, testPost.getLikes()[0]);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_like_post_when_post_not_exist() {
        // Given
        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);

        // When
        int result = postService.likeAPost(postId, token);

        // Then
        assertEquals(-1, result);
        verify(postRepository, never()).save(testPost);
    }

    // getAllLikesOnPost

    @Test
    public void should_get_all_likes() {
        // Given
        String[] likes = {"otherUser"};
        testPost.setLikes(likes);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        String[] responseLikes = postService.getAllLikesOnPost(postId);

        // Then
        assertNotNull(responseLikes);
        assertEquals(likes.length, responseLikes.length);
        assertEquals(likes, responseLikes);
    }

    @Test
    public void should_get_zero_likes_when_no_like_exists() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        String[] responseLikes = postService.getAllLikesOnPost(postId);

        // Then
        assertNotNull(responseLikes);
        assertEquals(0, responseLikes.length);
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_get_null_likes_when_post_does_not_exists() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        String[] responseLikes = postService.getAllLikesOnPost(postId);

        // Then
        assertNull(responseLikes);
        verify(postRepository, times(1)).findById(postId);
    }

    // removeAlikeOnPost

    @Test
    public void should_remove_like_on_post() {
        // Given
        testPost.setLikes(new String[]{"otherUser"});

        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.removeAlikeOnPost(postId, token);

        // Then
        assertEquals(1, result);
        assertEquals(0, testPost.getLikes().length);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_not_remove_like_on_post_when_user_has_not_liked_the_post() {
        // Given
        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(token.substring(7))).thenReturn(otherUsername);

        // When
        int result = postService.removeAlikeOnPost(postId, token);

        // Then
        assertEquals(0, result);
        assertNull(testPost.getLikes());
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_remove_like_on_post_when_post_does_not_exist() {
        // Given
        String token = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        int result = postService.removeAlikeOnPost(postId, token);

        // Then
        assertEquals(-1, result);
        assertNull(testPost.getLikes());
        verify(postRepository, never()).save(testPost);
    }

    // addHashtags

    @Test
    public void should_add_hashtags_for_a_post() {
        // Given
        String[] hashtags = {"viru", "rohirat", "partytime"};

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        String[] responseAddHashtags = postService.addHashtags(postId, hashtags);

        // Then
        assertNotNull(responseAddHashtags);
        assertEquals(hashtags.length, responseAddHashtags.length);
        assertEquals(hashtags[0], responseAddHashtags[0]);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_add_hashtags_to_existing_hashtags_for_a_post() {
        // Given
        testPost.setHashtags(new String[]{"existing", "notNew"});
        String[] hashtags = {"viru", "rohirat", "partytime"};

        String[] expectedHashtags = ArrayUtils.addAll(testPost.getHashtags(), hashtags);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        String[] responseAddHashtags = postService.addHashtags(postId, hashtags);

        // Then
        assertNotNull(responseAddHashtags);
        assertEquals(expectedHashtags.length, responseAddHashtags.length);
        assertEquals(expectedHashtags[0], responseAddHashtags[0]);
        assertEquals(expectedHashtags[expectedHashtags.length-1], responseAddHashtags[responseAddHashtags.length-1]);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_not_add_hashtags_when_post_not_exists() {
        // Given
        String[] hashtags = {"viru", "rohirat", "partytime"};

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        String[] responseAddHashtags = postService.addHashtags(postId, hashtags);

        // Then
        assertNull(responseAddHashtags);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(testPost);
    }

    // getHashtagsOfPost

    @Test
    public void should_get_hashtags_for_a_post() {
        // Given
        testPost.setHashtags(new String[]{"viru", "rohirat", "partytime"});

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        String[] responseAddHashtags = postService.getHashtagsOfPost(postId);

        // Then
        assertNotNull(responseAddHashtags);
        assertEquals(3, responseAddHashtags.length);
        assertEquals("viru", responseAddHashtags[0]);
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_get_zero_hashtags_when_no_hashtag_added_to_post() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        String[] responseAddHashtags = postService.getHashtagsOfPost(postId);

        // Then
        assertNotNull(responseAddHashtags);
        assertEquals(0, responseAddHashtags.length);
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_get_null_hashtags_when_post_does_not_exist() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        String[] responseAddHashtags = postService.getHashtagsOfPost(postId);

        // Then
        assertNull(responseAddHashtags);
        verify(postRepository, times(1)).findById(postId);
    }

    // updateHashtags

    @Test
    public void should_update_hashtags_for_a_post() {
        // Given
        testPost.setHashtags(new String[]{"viru", "rohirat", "partytime"});

        String[] newHashtags = {"newHashTag", "notOld"};

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        String[] responseAddHashtags = postService.updateHashtags(postId, newHashtags);

        // Then
        assertNotNull(responseAddHashtags);
        assertEquals(2, responseAddHashtags.length);
        assertEquals("newHashTag", responseAddHashtags[0]);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_update_hashtags_when_no_existing_hashtag_for_post() {
        // Given
        String[] newHashtags = {"newHashTag", "notOld"};

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        String[] responseAddHashtags = postService.updateHashtags(postId, newHashtags);

        // Then
        assertNotNull(responseAddHashtags);
        assertEquals(2, responseAddHashtags.length);
        assertEquals("newHashTag", responseAddHashtags[0]);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_not_update_hashtags_when_post_does_not_exist() {
        // Given
        String[] newHashtags = {"newHashTag", "notOld"};

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        String[] responseAddHashtags = postService.updateHashtags(postId, newHashtags);

        // Then
        assertNull(responseAddHashtags);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(testPost);
    }

    // deleteHashtagsOfPost

    @Test
    public void should_delete_a_hashtag_for_a_post() {
        // Given
        testPost.setHashtags(new String[]{"viru", "rohirat", "partytime"});

        String hashtagToBeDeleted = "rohirat";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.deleteHashtagsOfPost(postId, hashtagToBeDeleted);

        // Then
        assertEquals(1, result);
        assertEquals(2, testPost.getHashtags().length);
        assertEquals("partytime", testPost.getHashtags()[1]);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    public void should_not_delete_a_hashtag_when_hashtag_does_not_exist() {
        // Given
        testPost.setHashtags(new String[]{"viru", "rohirat", "partytime"});

        String hashtagToBeDeleted = "clubbing";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        int result = postService.deleteHashtagsOfPost(postId, hashtagToBeDeleted);

        // Then
        assertEquals(0, result);
        assertEquals(3, testPost.getHashtags().length);
        assertEquals("rohirat", testPost.getHashtags()[1]);
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository, never()).save(testPost);
    }

    @Test
    public void should_not_delete_a_hashtag_when_post_does_not_exist() {
        // Given
        String hashtagToBeDeleted = "clubbing";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        int result = postService.deleteHashtagsOfPost(postId, hashtagToBeDeleted);

        // Then
        assertEquals(-1, result);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(testPost);
    }

}