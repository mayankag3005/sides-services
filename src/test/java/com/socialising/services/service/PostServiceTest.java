package com.socialising.services.service;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

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
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private Long postId;
    private String adminUsername;
    private String ownerUsername;
    private String otherUsername;
    private User adminUser;
    private User ownerUser;
    private User otherUser;

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

        adminUsername = "adminUser";
        ownerUsername = "ownerUser";
        otherUsername = "otherUser";

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

//        Logger logger = (Logger) LoggerFactory.getLogger(PostService.class);
//        memoryAppender = new MemoryAppender();
//        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
//        logger.setLevel(Level.INFO);
//        logger.addAppender(memoryAppender);
//        memoryAppender.start();
    }

    @Test
    public void should_add_post() throws Exception {

        // Mock JWT token
        String mockJwtToken = "mock.jwt.token";

        // Mock username and user data
        when(jwtService.extractUsername(anyString())).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // Mock saving of post
        when(postRepository.save(any())).thenReturn(testPost);

        // When
        Post addedPost = postService.addPost(testPost, "Bearer " + mockJwtToken);

        // Then
        assertNotNull(addedPost);
        assertEquals(testPost.getDescription(), addedPost.getDescription());

        // Verify interactions
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(userRepository, times(1)).findByUsername(ownerUsername);
        verify(postRepository, times(1)).save(any());
    }

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

    @Test
    public void should_not_delete_post_if_not_exist() {
        // Given
        Long postId = 4L;
        String mockJwtToken = "mock.jwt.token";

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        postService.deletePost(postId, mockJwtToken);

        // Then
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).deleteById(postId);
    }

    @Test
    public void should_delete_post_when_user_is_authorized() {
//        log = LoggerFactory.getLogger(PostService.class);

        // Mock JWT token
        String mockJwtToken = "Bearer valid.jwt.token";

        // Mock username and user data
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(ownerUsername);
        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.of(ownerUser));

        // When
        postService.deletePost(postId, mockJwtToken);

        // Then
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository).deleteById(postId);
//        assertEquals(log.info()).info("Post with Post ID: {} deleted from DB", postId);
    }

    @Test
    public void should_delete_post_when_user_is_admin() {
//        log = LoggerFactory.getLogger(PostService.class);

        // Mock JWT token
        String mockJwtToken = "Bearer admin.jwt.token";

        // Mock username and user data
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(adminUsername);
        when(userRepository.findByUsername(adminUsername)).thenReturn(Optional.of(adminUser));

        // When
        postService.deletePost(postId, mockJwtToken);

        // Then
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
        postService.deletePost(postId, token);

        // Then
        verify(postRepository, times(2)).findById(postId);
        verify(postRepository, never()).deleteById(postId);
    }

    // Test for deleting post from confirm users reminder posts list, when a post is deleted

    // Test for deleting post from interested users requested posts list, when a post is deleted


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

    // This method checks if a post exists in the database, retrieves the list of interested users, logs the appropriate message, and returns the usernames of the interested users.
    @Test
    public void should_get_interested_users_when_users_exist() {
        // Given
        List<User> interestedUsers = new ArrayList<>();
        interestedUsers.add(otherUser);
        testPost.setInterestedUsers(interestedUsers);

        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        List<String> responseInterestedUsers = postService.getInterestedUsers(postId);

        // Then
        assertNotNull(responseInterestedUsers);
        assertEquals(interestedUsers.size(), responseInterestedUsers.size());
        assertEquals(interestedUsers.get(0).getUsername(), responseInterestedUsers.get(0));
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_not_get_interested_users_when_users_not_exist() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        // When
        List<String> responseInterestedUsers = postService.getInterestedUsers(postId);

        // Then
        assertNotNull(responseInterestedUsers);
        assertEquals(0, responseInterestedUsers.size());
        verify(postRepository, times(2)).findById(postId);
    }

    @Test
    public void should_get_null_for_interested_users_when_post_does_not_exist() {
        // Mock
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When
        List<String> responseInterestedUsers = postService.getInterestedUsers(postId);

        // Then
        assertNull(responseInterestedUsers);
        verify(postRepository, times(1)).findById(postId);
    }
}