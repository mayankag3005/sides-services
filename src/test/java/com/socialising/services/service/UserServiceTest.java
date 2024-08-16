package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.Role;
import com.socialising.services.constants.Status;
import com.socialising.services.model.ChangePasswordRequest;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.model.nosql.ImageMongo;
import com.socialising.services.model.token.Token;
import com.socialising.services.repository.*;
import com.socialising.services.repository.nosql.ImageMongoRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageMongoRepository imageMongoRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Long testUserId;
    private Long secondTestUserId;
    private Long adminTestUserId;
    private String testUsername;
    private String secondTestUsername;
    private String adminTestUsername;
    private User testUser;
    private User secondTestUser;
    private User adminTestUser;

    private Long postId;
    private Long secondPostId;
    private Long thirdPostId;
    private Post testUserPost;
    private Post secondTestUserPost;
    private Post secondTestUserSecondPost;

    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        secondTestUserId = 2L;
        adminTestUserId = 3L;

        testUsername = "test-user";
        secondTestUsername = "second-test-user";
        adminTestUsername = "admin-test-user";

        testUser = User.builder()
                .userId(testUserId)
                .username(testUsername)
                .phoneNumber("1234567890")
                .email("test-user@example.com")
                .role(Role.USER)
                .build();

        secondTestUser = User.builder()
                .userId(secondTestUserId)
                .username(secondTestUsername)
                .phoneNumber("1234567891")
                .email("second-test-user@example.com")
                .role(Role.USER)
                .build();

        adminTestUser = User.builder()
                .userId(adminTestUserId)
                .username(adminTestUsername)
                .phoneNumber("1234567892")
                .email("admin-test-user@example.com")
                .role(Role.ADMIN)
                .build();

        postId = 20L;
        secondPostId = 21L;
        thirdPostId = 22L;

        testUserPost = Post.builder()
                .postId(postId)
                .ownerUser(testUser)
                .description("This is first test post of first test user")
                .postType("general")
                .timeType("later")
                .postStartTs("2024-07-13")
                .postEndTs("2024-08-15")
                .location("Amity")
                .onlyForWomen('N')
                .confirmedUsers(new ArrayList<>())
                .interestedUsers(new ArrayList<>())
                .build();

        testUser.setPosts(List.of(testUserPost));

        secondTestUserPost = Post.builder()
                .postId(secondPostId)
                .ownerUser(secondTestUser)
                .description("This is first test post of second test user")
                .postType("general")
                .timeType("later")
                .postStartTs("2024-06-21")
                .postEndTs("2024-06-22")
                .location("Srinagar")
                .onlyForWomen('Y')
                .confirmedUsers(new ArrayList<>())
                .interestedUsers(new ArrayList<>())
                .build();

        secondTestUserSecondPost = Post.builder()
                .postId(thirdPostId)
                .ownerUser(secondTestUser)
                .description("This is second test post of second test user")
                .postType("general")
                .timeType("later")
                .postStartTs("2024-05-23")
                .postEndTs("2024-07-24")
                .location("Twang")
                .onlyForWomen('Y')
                .confirmedUsers(new ArrayList<>())
                .interestedUsers(new ArrayList<>())
                .build();

        secondTestUser.setPosts(List.of(secondTestUserPost, secondTestUserSecondPost ));
    }

    // addUser

    @Test
    public void should_add_user_by_admin_only() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(adminTestUsername);
        when(userRepository.findByUsername(adminTestUsername)).thenReturn(Optional.of(adminTestUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User responseUser = userService.addUser(testUser, mockJwtToken);

        // Then
        assertNotNull(responseUser);
        assertEquals(testUser.getUsername(), responseUser.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void should_not_add_user_if_user_not_admin() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        User responseUser = userService.addUser(testUser, mockJwtToken);

        // Then
        assertNull(responseUser);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    public void should_not_add_user_when_save_fails() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(adminTestUsername);
        when(userRepository.findByUsername(adminTestUsername)).thenReturn(Optional.of(adminTestUser));
        when(userRepository.save(testUser)).thenThrow(new RuntimeException("Database error"));

        // When
        User responseUser = userService.addUser(testUser, mockJwtToken);

        // Then
        assertNull(responseUser);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void should_not_add_user_when_passed_invalid_token() {
        // Given
        String mockJwtToken = "Bearer invalid.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // When
        User responseUser = userService.addUser(testUser, mockJwtToken);

        // Then
        assertNull(responseUser);
        verify(userRepository, never()).findByUsername(adminTestUsername);
        verify(userRepository, never()).save(testUser);
    }

    // getAllUserDetails

    @Test
    public void should_get_all_users_details_from_db() {
        // Given
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(testUser);
        expectedUsers.add(secondTestUser);

        // Mock
        when(userRepository.count()).thenReturn(2L);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> responseAllUsers = userService.getAllUserDetails();

        // Then
        assertEquals(expectedUsers.size(), responseAllUsers.size());
        assertEquals(expectedUsers.get(0).getUsername(), responseAllUsers.get(0).getUsername());
        assertEquals(expectedUsers.get(1).getUsername(), responseAllUsers.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void should_get_zero_users_details_from_db_when_there_are_no_users() {
        // Mock
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<User> responseAllUsers = userService.getAllUserDetails();

        // Then
        assertEquals(0, responseAllUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    // getUserDetails

    @Test
    public void should_get_user_details() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        User responseUser = userService.getUserDetails(mockJwtToken);

        // Then
        assertNotNull(responseUser);
        assertEquals(testUser.getUsername(), responseUser.getUsername());
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    public void should_not_get_user_when_user_not_found() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        // When
        User responseUser = userService.getUserDetails(mockJwtToken);

        // Then
        assertNull(responseUser);
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    public void should_not_get_user_when_throws_not_found_exception() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenThrow(new UsernameNotFoundException("User not found"));

        // When
        User responseUser = userService.getUserDetails(mockJwtToken);

        // Then
        assertNull(responseUser);
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    public void should_not_get_user_when_throws_jwt_exception() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // When
        User responseUser = userService.getUserDetails(mockJwtToken);

        // Then
        assertNull(responseUser);
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, never()).findByUsername(testUsername);
    }

    // getUserById

    @Test
    public void should_get_user_details_when_correct_id_passed() {
        // Mock
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        User responseUser = userService.getUserById(1L);

        // Then
        assertNotNull(responseUser);
        assertEquals(testUser.getUsername(), responseUser.getUsername());
        verify(userRepository, times(2)).findById(1L);
    }

    @Test
    public void should_not_get_user_details_when_incorrect_id_passed() {
        // Mock
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        // When
        User responseUser = userService.getUserById(3L);

        // Then
        assertNull(responseUser);
        verify(userRepository, times(1)).findById(3L);
    }

    // getUserByUsername

    @Test
    public void should_get_user_details_when_correct_username_passed() {
        // Mock
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        User responseUser = userService.getUserByUsername(testUsername);

        // Then
        assertNotNull(responseUser);
        assertEquals(testUser.getUsername(), responseUser.getUsername());
        verify(userRepository, times(2)).findByUsername(testUsername);
    }

    @Test
    public void should_not_get_user_details_when_incorrect_username_passed() {
        // Given
        String username = "third-test-user";

        // Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        User responseUser = userService.getUserByUsername(username);

        // Then
        assertNull(responseUser);
        verify(userRepository, times(1)).findByUsername(username);
    }

    // getUserByPhoneNumber

    @Test
    public void should_get_user_details_when_correct_phone_number_passed() {
        // Given
        String phoneNumber = "1234567890";

        // Mock
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(testUser));

        // When
        User responseUser = userService.getUserByPhoneNumber(phoneNumber);

        // Then
        assertNotNull(responseUser);
        assertEquals(testUser.getUsername(), responseUser.getUsername());
        verify(userRepository, times(2)).findByPhoneNumber(phoneNumber);
    }

    @Test
    public void should_not_get_user_details_when_incorrect_phone_number_passed() {
        // Given
        String phoneNumber = "1234567888";

        // Mock
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        // When
        User responseUser = userService.getUserByPhoneNumber(phoneNumber);

        // Then
        assertNull(responseUser);
        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    // updateUserDetailsExceptUsernamePasswordAndDP

    @Test
    public void should_update_user_details_when_user_is_authorized() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        testUser.setPassword("password");

        User userToUpdate = new User();
        userToUpdate.setUsername(testUsername);
        userToUpdate.setFirstName("NewFirstName");
        userToUpdate.setLastName("NewLastName");
        userToUpdate.setEmail("newemail@example.com");
        userToUpdate.setDob("1990-01-01");
        userToUpdate.setAge(34);
        userToUpdate.setGender("Male");
        userToUpdate.setReligion("Religion");
        userToUpdate.setEducation("Education");
        userToUpdate.setOccupation("Occupation");
        userToUpdate.setMaritalStatus("Single");
        userToUpdate.setCity("City");
        userToUpdate.setState("State");
        userToUpdate.setHomeCity("HomeCity");
        userToUpdate.setHomeState("HomeState");
        userToUpdate.setCountry("Country");

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User updatedUser = userService.updateUserDetailsExceptUsernamePasswordAndDP(userToUpdate, mockJwtToken);

        // Then
        assertNotNull(updatedUser);
        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("NewLastName", updatedUser.getLastName());
        assertEquals("HomeCity", updatedUser.getHomeCity());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void should_not_update_user_details_when_not_authorized() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        User userToUpdate = new User();
        userToUpdate.setUsername(secondTestUsername);
        userToUpdate.setRole(Role.USER);

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);

        // When
        User responseUser = userService.updateUserDetailsExceptUsernamePasswordAndDP(userToUpdate, mockJwtToken);

        // Then
        assertNull(responseUser);
        verify(userRepository, never()).save(any());
    }

    // deleteUser
    // Only ADMIN can delete a User

    @Test
    public void should_delete_user_when_exist_in_DB() {
        User friendRequestedUser1 = User.builder()
                        .userId(6L)
                        .username("friend-requested-user-1")
                        .friendRequests(new String[]{testUsername})
                        .build();

        User friendRequestedUser2 = User.builder()
                .userId(7L)
                .username("friend-requested-user-2")
                .friendRequests(new String[]{testUsername})
                .build();

        User friendUser1 = User.builder()
                .userId(8L)
                .username("friend-user-1")
                .friends(new String[]{testUsername})
                .build();

        User friendUser2 = User.builder()
                .userId(9L)
                .username("friend-user-2")
                .friends(new String[]{testUsername})
                .build();

        testUser.setFriendsRequested(new String[]{"friend-requested-user-1", "friend-requested-user-2"});
        testUser.setFriends(new String[]{"friend-user-1", "friend-user-2"});

        secondTestUserPost.setInterestedUsers(new ArrayList<>(Collections.singletonList(testUser)));
        testUser.setRequestedPosts(List.of(secondTestUserPost));
        secondTestUserSecondPost.setConfirmedUsers(new ArrayList<>(Collections.singletonList(testUser)));
        testUser.setReminderPosts(List.of(secondTestUserSecondPost));

        Token token1 = new Token();
        token1.setId(1);
        token1.setUser(testUser);

        Token token2 = new Token();
        token2.setId(2);
        token2.setUser(testUser);

        List<Token> validUserTokens = List.of(token1, token2);

        // Mock
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(tokenRepository.findAllValidTokens(testUserId)).thenReturn(validUserTokens);
        when(userRepository.findByUsername("friend-requested-user-1")).thenReturn(Optional.of(friendRequestedUser1));
        when(userRepository.findByUsername("friend-requested-user-2")).thenReturn(Optional.of(friendRequestedUser2));
        when(userRepository.findByUsername("friend-user-1")).thenReturn(Optional.of(friendUser1));
        when(userRepository.findByUsername("friend-user-2")).thenReturn(Optional.of(friendUser2));
        when(userRepository.save(friendRequestedUser1)).thenReturn(friendRequestedUser1);
        when(userRepository.save(friendRequestedUser2)).thenReturn(friendRequestedUser2);
        when(userRepository.save(friendUser1)).thenReturn(friendUser1);
        when(userRepository.save(friendUser2)).thenReturn(friendUser2);
        when(postRepository.save(secondTestUserPost)).thenReturn(secondTestUserPost);
        when(postRepository.save(secondTestUserSecondPost)).thenReturn(secondTestUserSecondPost);

        // When
        int result = userService.deleteUser(testUserId);

        // Then
        assertEquals(1, result);
        assertEquals(0, friendRequestedUser1.getFriendRequests().length);
        assertEquals(0, friendRequestedUser2.getFriendRequests().length);
        assertEquals(0, friendUser1.getFriends().length);
        assertEquals(0, friendUser2.getFriends().length);
        assertEquals(0, secondTestUserPost.getInterestedUsers().size());
        assertEquals(0, secondTestUserSecondPost.getConfirmedUsers().size());

        verify(tokenRepository, times(2)).deleteById(anyInt());
        verify(userRepository, times(1)).deleteById(testUserId);
        verify(userRepository, times(4)).save(any());
        verify(postRepository, times(2)).save(any());
    }

    @Test
    public void should_not_delete_user_when_user_does_not_exist_in_DB() {
        // Mock
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When
        int result = userService.deleteUser(testUserId);

        // Then
        assertEquals(-1, result);

        verify(userRepository, times(1)).findById(testUserId);
        verify(userRepository, never()).deleteById(testUserId);
    }

    // searchUserByWord

    @Test
    public void should_give_users_having_word_in_username() {
        // Given
        String word = "test-user";

        List<User> expectedSearchedUsers = Arrays.asList(testUser, secondTestUser, adminTestUser);

        // Mock
        when(userRepository.searchUserByWord(word)).thenReturn(expectedSearchedUsers);

        // When
        List<User> responseSearchedUsers = userService.searchUserByWord(word);

        // Then
        assertNotNull(responseSearchedUsers);
        assertEquals(3, responseSearchedUsers.size());
        verify(userRepository, times(1)).searchUserByWord(word);
    }

    @Test
    public void should_give_zero_users_having_word_in_username() {
        // Given
        String word = "halo";

        List<User> expectedSearchedUsers = List.of();

        // Mock
        when(userRepository.searchUserByWord(word)).thenReturn(expectedSearchedUsers);

        // When
        List<User> responseSearchedUsers = userService.searchUserByWord(word);

        // Then
        assertNotNull(responseSearchedUsers);
        assertEquals(0, responseSearchedUsers.size());
        verify(userRepository, times(1)).searchUserByWord(word);
    }

    // searchUserByTag

    @Test
    public void should_get_users_matching_tag_passed_as_word() {
        // Given
        String tagWord = "soccer";

        testUser.setTags(new String[]{"soccer", "club-hopping"});
        secondTestUser.setTags(new String[]{"cafe-hopping", "cricket"});
        adminTestUser.setTags(new String[]{"cricket", "soccer"});

        List<User> expectedUsers = List.of(testUser, adminTestUser);

        // Mock
        when(userRepository.findAll()).thenReturn(List.of(testUser, secondTestUser, adminTestUser));

        // When
        List<User> responseUsers = userService.searchUserByTag(tagWord);

        // Then
        assertEquals(expectedUsers.size(), responseUsers.size());
        assertEquals(expectedUsers.get(0).getUsername(), responseUsers.get(0).getUsername());
        assertEquals(expectedUsers.get(1).getUsername(), responseUsers.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void should_get_zero_users_matching_tag_passed_as_word() {
        // Given
        String tagWord = "tennis";

        testUser.setTags(new String[]{"soccer", "club-hopping"});
        secondTestUser.setTags(new String[]{"cafe-hopping", "cricket"});
        adminTestUser.setTags(new String[]{"cricket", "soccer"});

        // Mock
        when(userRepository.findAll()).thenReturn(List.of(testUser, secondTestUser, adminTestUser));

        // When
        List<User> responseUsers = userService.searchUserByTag(tagWord);

        // Then
        assertEquals(0, responseUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    // sendFriendRequest
    /** TEST_USER is FROM_USER
     * SECOND_TEST_USER is TO_USER
     * TEST_USER is sending Friend Request to TO_USER
     */

    @Test
    public void should_not_send_request_when_to_user_does_not_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.empty());

        // When
        String result = userService.sendFriendRequest(secondTestUsername, mockJwtToken);

        // Then
        assertEquals("User [" + secondTestUsername + "] does not exist in DB", result);
        verify(userRepository, never()).save(secondTestUser);
    }

    @Test
    public void should_not_send_request_when_users_are_already_friends() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriends(new String[]{secondTestUsername});
        secondTestUser.setFriends(new String[]{testUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        String result = userService.sendFriendRequest(secondTestUsername, mockJwtToken);

        // Then
        assertEquals("User [" + secondTestUsername + "] is already friends with User [" + testUsername + "]", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void should_not_send_request_when_friend_request_already_sent() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriendsRequested(new String[]{secondTestUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        String result = userService.sendFriendRequest(secondTestUsername, mockJwtToken);

        // Then
        assertEquals("Friend Request Already Sent", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void should_not_send_request_when_friend_request_already_received() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        secondTestUser.setFriendRequests(new String[]{testUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        String result = userService.sendFriendRequest(secondTestUsername, mockJwtToken);

        // Then
        assertEquals("Friend Request Already Sent", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void should_send_friend_request() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.save(secondTestUser)).thenReturn(secondTestUser);

        // When
        String result = userService.sendFriendRequest(secondTestUsername, mockJwtToken);

        // Then
        assertEquals("Friend request Sent", result);
        assertEquals(1, testUser.getFriendsRequested().length);
        assertEquals(1, secondTestUser.getFriendRequests().length);
        assertEquals(secondTestUsername, testUser.getFriendsRequested()[0]);
        assertEquals(testUsername, secondTestUser.getFriendRequests()[0]);
        verify(userRepository, times(1)).save(testUser);
        verify(userRepository, times(1)).save(secondTestUser);
    }

    // acceptFriendRequest
    /** TEST_USER is FROM_USER
     * SECOND_TEST_USER is TO_USER
     * TEST_USER is sending Friend Request to TO_USER
     */

    @Test
    public void should_not_accept_request_when_already_friends() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        secondTestUser.setFriends(new String[]{testUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        String result = userService.acceptFriendRequest(testUsername, mockJwtToken);

        // Then
        assertEquals("Already Friends", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void should_not_accept_request_when_not_sent() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        String result = userService.acceptFriendRequest(testUsername, mockJwtToken);

        // Then
        assertEquals("Friend Request NOT Sent", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void should_not_accept_request_when_fromUser_does_not_exist_anymore() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        secondTestUser.setFriendRequests(new String[]{testUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());
        when(userRepository.save(secondTestUser)).thenReturn(secondTestUser);

        // When
        String result = userService.acceptFriendRequest(testUsername, mockJwtToken);

        // Then
        assertEquals("User [" + testUsername + "] does not exist in DB", result);
        verify(userRepository, times(1)).save(secondTestUser);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    public void should_accept_request_when_request_is_sent() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriendsRequested(new String[]{secondTestUsername});
        secondTestUser.setFriendRequests(new String[]{testUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.save(secondTestUser)).thenReturn(secondTestUser);

        // When
        String result = userService.acceptFriendRequest(testUsername, mockJwtToken);

        // Then
        assertEquals("Friend request accepted", result);
        assertEquals(0, testUser.getFriendsRequested().length);
        assertEquals(0, secondTestUser.getFriendRequests().length);
        assertFalse(ArrayUtils.contains(testUser.getFriendsRequested(), secondTestUsername));
        assertFalse(ArrayUtils.contains(secondTestUser.getFriendRequests(), testUsername));
        assertEquals(1, testUser.getFriends().length);
        assertEquals(1, secondTestUser.getFriends().length);
        assertTrue(ArrayUtils.contains(testUser.getFriends(), secondTestUsername));
        assertTrue(ArrayUtils.contains(secondTestUser.getFriends(), testUsername));
        verify(userRepository, times(1)).save(secondTestUser);
        verify(userRepository, times(1)).save(testUser);
    }

    // deleteFriendRequest
    /** TEST_USER is FROM_USER
     * SECOND_TEST_USER is TO_USER
     * TEST_USER is sending Friend Request to TO_USER
     */

    @Test
    public void should_not_reject_request_when_request_not_sent() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        String result = userService.deleteFriendRequest(testUsername, mockJwtToken);

        // Then
        assertEquals("Friend Request NOT Sent", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void should_reject_request_even_when_fromUser_does_not_exist_anymore_and_delete_the_request() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        secondTestUser.setFriendRequests(new String[]{testUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());
        when(userRepository.save(secondTestUser)).thenReturn(secondTestUser);

        // When
        String result = userService.deleteFriendRequest(testUsername, mockJwtToken);

        // Then
        assertEquals("Friend request Deleted", result);
        assertEquals(0, secondTestUser.getFriendRequests().length);
        assertFalse(ArrayUtils.contains(secondTestUser.getFriendRequests(), testUsername));
        verify(userRepository, times(1)).save(secondTestUser);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    public void should_reject_request_when_fromUser_exist_anymore_and_delete_the_request() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriendsRequested(new String[]{secondTestUsername});
        secondTestUser.setFriendRequests(new String[]{testUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.save(secondTestUser)).thenReturn(secondTestUser);

        // When
        String result = userService.deleteFriendRequest(testUsername, mockJwtToken);

        // Then
        assertEquals("Friend request Deleted", result);
        assertEquals(0, testUser.getFriendsRequested().length);
        assertEquals(0, secondTestUser.getFriendRequests().length);
        assertFalse(ArrayUtils.contains(testUser.getFriendsRequested(), secondTestUsername));
        assertFalse(ArrayUtils.contains(secondTestUser.getFriendRequests(), testUsername));
        verify(userRepository, times(1)).save(secondTestUser);
        verify(userRepository, times(1)).save(testUser);
    }

    // getFriendRequestUsers

    @Test
    public void should_get_usernames_of_friend_request_users() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriendRequests(new String[]{secondTestUsername, adminTestUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        String[] responseFriendRequestUsers = userService.getFriendRequestUsers(mockJwtToken);

        // Then
        assertNotNull(responseFriendRequestUsers);
        assertEquals(2, responseFriendRequestUsers.length);
        assertEquals(secondTestUsername, responseFriendRequestUsers[0]);
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    public void should_get_zero_usernames_of_friend_request_users_when_no_friend_request_received() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        String[] responseFriendRequestUsers = userService.getFriendRequestUsers(mockJwtToken);

        // Then
        assertNotNull(responseFriendRequestUsers);
        assertEquals(0, responseFriendRequestUsers.length);
        assertFalse(ArrayUtils.contains(responseFriendRequestUsers, secondTestUsername));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    // getFriendsRequested

    @Test
    public void should_get_usernames_of_friends_requested_users() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriendsRequested(new String[]{secondTestUsername, adminTestUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        String[] responseFriendsRequestedUsernames = userService.getFriendsRequested(mockJwtToken);

        // Then
        assertNotNull(responseFriendsRequestedUsernames);
        assertEquals(2, responseFriendsRequestedUsernames.length);
        assertTrue(ArrayUtils.contains(responseFriendsRequestedUsernames, secondTestUsername));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    public void should_get_zero_usernames_of_friends_requested_users_when_no_friend_is_requested() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        String[] responseFriendsRequestedUsernames = userService.getFriendsRequested(mockJwtToken);

        // Then
        assertNotNull(responseFriendsRequestedUsernames);
        assertEquals(0, responseFriendsRequestedUsernames.length);
        assertFalse(ArrayUtils.contains(responseFriendsRequestedUsernames, secondTestUsername));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    // getFriendsOfUser

    @Test
    public void should_get_usernames_of_friends_users() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriends(new String[]{secondTestUsername, adminTestUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        String[] responseFriendsUsernames = userService.getFriendsOfUser(mockJwtToken);

        // Then
        assertNotNull(responseFriendsUsernames);
        assertEquals(2, responseFriendsUsernames.length);
        assertTrue(ArrayUtils.contains(responseFriendsUsernames, secondTestUsername));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    public void should_get_zero_usernames_of_friends_users_when_no_friend_is_present() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        String[] responseFriendsUsernames = userService.getFriendsOfUser(mockJwtToken);

        // Then
        assertNotNull(responseFriendsUsernames);
        assertEquals(0, responseFriendsUsernames.length);
        assertFalse(ArrayUtils.contains(responseFriendsUsernames, secondTestUsername));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    // deleteFriend

    @Test
    public void should_not_delete_friend_when_not_friends() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // when
        int result = userService.deleteFriend(secondTestUsername, mockJwtToken);

        // Then
        assertEquals(-1, result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void test_delete_friend_when_friend_does_not_exist_but_friend_username_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriends(new String[]{secondTestUsername, adminTestUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.empty());
        when(userRepository.save(testUser)).thenReturn(testUser);

        // when
        int result = userService.deleteFriend(secondTestUsername, mockJwtToken);

        // Then
        assertEquals(1, result);
        assertEquals(1, testUser.getFriends().length);
        assertFalse(ArrayUtils.contains(testUser.getFriends(), secondTestUsername));
        verify(userRepository, times(1)).save(testUser);
        verify(userRepository, never()).save(secondTestUser);
    }

    @Test
    public void should_delete_friend_when_friends() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setFriends(new String[]{secondTestUsername, adminTestUsername});
        secondTestUser.setFriends(new String[]{testUsername});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.save(secondTestUser)).thenReturn(secondTestUser);

        // when
        int result = userService.deleteFriend(secondTestUsername, mockJwtToken);

        // Then
        assertEquals(1, result);
        assertEquals(1, testUser.getFriends().length);
        assertEquals(0, secondTestUser.getFriends().length);
        assertFalse(ArrayUtils.contains(testUser.getFriends(), secondTestUsername));
        assertFalse(ArrayUtils.contains(secondTestUser.getFriends(), testUsername));
        verify(userRepository, times(1)).save(testUser);
        verify(userRepository, times(1)).save(secondTestUser);
    }

    // getPostsOfUser

    @Test
    public void should_get_all_posts_Of_User() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        List<Post> responsePostsOfUser = userService.getPostsOfUser(mockJwtToken);

        // Then
        assertNotNull(responsePostsOfUser);
        assertEquals(2, responsePostsOfUser.size());
        assertEquals(secondPostId, responsePostsOfUser.get(0).getPostId());
    }

    @Test
    public void should_get_null_posts_Of_User() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(adminTestUsername);
        when(userRepository.findByUsername(adminTestUsername)).thenReturn(Optional.of(adminTestUser));

        // When
        List<Post> responsePostsOfUser = userService.getPostsOfUser(mockJwtToken);

        // Then
        assertNull(responsePostsOfUser);
        verify(userRepository, times(1)).findByUsername(adminTestUsername);
    }

    // getRequestedPostsOfUser

    @Test
    public void should_get_all_requested_posts_Of_User() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setRequestedPosts(List.of(secondTestUserPost));

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        List<Post> responseRequestedPostsOfUser = userService.getRequestedPostsOfUser(mockJwtToken);

        // Then
        assertNotNull(responseRequestedPostsOfUser);
        assertEquals(1, responseRequestedPostsOfUser.size());
        assertEquals(secondPostId, responseRequestedPostsOfUser.get(0).getPostId());
    }

    @Test
    public void should_get_null_requested_posts_Of_User_when_user_not_requested_any_post() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        List<Post> responseRequestedPostsOfUser = userService.getRequestedPostsOfUser(mockJwtToken);

        // Then
        assertNull(responseRequestedPostsOfUser);
        verify(userRepository, times(1)).findByUsername(secondTestUsername);
    }

    // getReminderPosts

    @Test
    public void should_get_all_reminder_posts_Of_User() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setReminderPosts(List.of(secondTestUserSecondPost));

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        List<Post> responseReminderPostsOfUser = userService.getReminderPosts(mockJwtToken);

        // Then
        assertNotNull(responseReminderPostsOfUser);
        assertEquals(1, responseReminderPostsOfUser.size());
        assertEquals(thirdPostId, responseReminderPostsOfUser.get(0).getPostId());
    }

    @Test
    public void should_get_null_reminder_posts_Of_User_when_user_not_requested_any_post() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        List<Post> responseReminderPostsOfUser = userService.getReminderPosts(mockJwtToken);

        // Then
        assertNull(responseReminderPostsOfUser);
        verify(userRepository, times(1)).findByUsername(secondTestUsername);
    }

    // deleteReminderPostsOfUser

    @Test
    public void should_not_delete_post_when_post_does_not_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(4L)).thenReturn(Optional.empty());

        // When
        List<Post> responseRemainingReminderPosts = userService.deleteReminderPostsOfUser(4L, mockJwtToken);

        // Then
        assertNull(responseRemainingReminderPosts);
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(postRepository, times(1)).findById(4L);
    }

    @Test
    public void should_not_delete_post_when_post_not_in_user_reminder_post_list() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(thirdPostId)).thenReturn(Optional.of(secondTestUserSecondPost));

        // When
        List<Post> responseRemainingReminderPosts = userService.deleteReminderPostsOfUser(thirdPostId, mockJwtToken);

        // Then
        assertNull(responseRemainingReminderPosts);
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(postRepository, times(2)).findById(thirdPostId);
        verify(userRepository, never()).save(any(User.class));
        verify(postRepository, never()).save(any(Post.class));
    }


    // This should not be a case. Post exist in Reminder List but User does not exist in Confirmed User list. Something is WRONG!!!
    @Test
    public void should_not_delete_post_when_user_not_in_post_confirmed_user_list() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setReminderPosts(new ArrayList<>(List.of(secondTestUserPost, secondTestUserSecondPost)));

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(thirdPostId)).thenReturn(Optional.of(secondTestUserSecondPost));

        // When
        List<Post> responseRemainingReminderPosts = userService.deleteReminderPostsOfUser(thirdPostId, mockJwtToken);

        // Then
        assertNull(responseRemainingReminderPosts);
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(postRepository, times(2)).findById(thirdPostId);
        verify(userRepository, never()).save(any(User.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    public void should_delete_post_when_user_in_post_confirmed_user_list_and_post_in_user_reminder_post_list() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setReminderPosts(new ArrayList<>(List.of(secondTestUserPost, secondTestUserSecondPost)));
        secondTestUserSecondPost.setConfirmedUsers(new ArrayList<>(List.of(testUser, adminTestUser)));

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(postRepository.findById(thirdPostId)).thenReturn(Optional.of(secondTestUserSecondPost));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(postRepository.save(secondTestUserSecondPost)).thenReturn(secondTestUserSecondPost);

        // When
        List<Post> responseRemainingReminderPosts = userService.deleteReminderPostsOfUser(thirdPostId, mockJwtToken);

        // Then
        assertNotNull(responseRemainingReminderPosts);
        assertFalse(responseRemainingReminderPosts.contains(secondTestUserSecondPost));
        assertTrue(responseRemainingReminderPosts.contains(secondTestUserPost));
        assertFalse(testUser.getReminderPosts().contains(secondTestUserSecondPost));
        assertFalse(secondTestUserSecondPost.getConfirmedUsers().contains(testUser));
        assertTrue(testUser.getReminderPosts().contains(secondTestUserPost));
        assertTrue(secondTestUserSecondPost.getConfirmedUsers().contains(adminTestUser));
        assertEquals(1, testUser.getReminderPosts().size());
        assertEquals(1, secondTestUserSecondPost.getConfirmedUsers().size());
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(postRepository, times(2)).findById(thirdPostId);
        verify(userRepository, times(1)).save(testUser);
        verify(postRepository, times(1)).save(secondTestUserSecondPost);
    }

    // getTagsOfUser

    @Test
    public void should_get_tags_Of_user() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setTags(new String[]{"soccer", "club-hopping"});

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        String[] responseTagsOfUser = userService.getTagsOfUser(mockJwtToken);

        // Then
        assertNotNull(responseTagsOfUser);
        assertEquals(2, responseTagsOfUser.length);
        assertTrue(ArrayUtils.contains(responseTagsOfUser, "club-hopping"));
    }

    @Test
    public void should_get_zero_tags_Of_user_when_no_tags_added_by_user() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        String[] responseTagsOfUser = userService.getTagsOfUser(mockJwtToken);

        // Then
        assertNotNull(responseTagsOfUser);
        assertEquals(0, responseTagsOfUser.length);
        assertFalse(ArrayUtils.contains(responseTagsOfUser, "club-hopping"));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    // updateTagsOfUser

    @Test
    public void should_update_tags_Of_user() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setTags(new String[]{"soccer", "club-hopping"});
        String[] newTags = {"cricket", "coffee-hopping", "book-reading"};

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        String[] responseTagsOfUser = userService.updateTagsOfUser(newTags, mockJwtToken);

        // Then
        assertNotNull(responseTagsOfUser);
        assertEquals(3, responseTagsOfUser.length);
        assertFalse(ArrayUtils.contains(responseTagsOfUser, "club-hopping"));
        assertTrue(ArrayUtils.contains(responseTagsOfUser, "coffee-hopping"));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void should_set_zero_tags_Of_user_when_zero_tags_updated_by_user() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setTags(new String[]{"soccer", "club-hopping"});
        String[] newTags = {};

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        String[] responseTagsOfUser = userService.updateTagsOfUser(newTags, mockJwtToken);

        // Then
        assertNotNull(responseTagsOfUser);
        assertEquals(0, responseTagsOfUser.length);
        assertFalse(ArrayUtils.contains(responseTagsOfUser, "club-hopping"));
        verify(userRepository, times(1)).save(testUser);
    }

    // addUserDP

    @Test
    public void should_not_add_dp_when_file_is_null() throws Exception {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // When
        ImageMongo result = userService.addUserProfilePicture(null, mockJwtToken);

        // Then
        assertNull(result);
        verify(jwtService, never()).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, never()).findByUsername(testUsername);
        verify(imageMongoRepository, never()).save(any(ImageMongo.class));
    }

    @Test
    public void should_not_add_dp_when_file_is_empty() throws Exception {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        // When
        ImageMongo result = userService.addUserProfilePicture(file, mockJwtToken);

        // Then
        assertNull(result);
        verify(jwtService, never()).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, never()).findByUsername(testUsername);
        verify(imageMongoRepository, never()).save(any(ImageMongo.class));
    }

    @Test
    public void should_add_add_dp_when_file_is_proper() throws Exception {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "some image content".getBytes()
        );

        String imageId = "654321";
        ImageMongo expectedImage = new ImageMongo();
        expectedImage.setId(imageId);
        expectedImage.setFileName("test.jpg");
        expectedImage.setFormat("image/jpeg");
        expectedImage.setData("some image content".getBytes());

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(imageMongoRepository.findByAssociatedUsername(testUsername)).thenReturn(Optional.empty());
        when(imageMongoRepository.save(any(ImageMongo.class))).thenReturn(expectedImage);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        ImageMongo result = userService.addUserProfilePicture(file, mockJwtToken);

        // Then
        assertNotNull(result);
        assertNotEquals("0", result.getId());
        assertEquals("test.jpg", result.getFileName());
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(userRepository, times(1)).save(testUser);
        verify(imageMongoRepository, times(1)).save(any(ImageMongo.class));
    }

    @Test
    public void should_add_dp_when_file_is_proper_and_remove_old_user_dp() throws Exception {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setUserDPId("123456");
        ImageMongo existingImage = new ImageMongo();
        existingImage.setId("123456");
        existingImage.setFileName("old.jpg");
        existingImage.setFormat("image/jpeg");
        existingImage.setData("old image content".getBytes());

        file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "some image content".getBytes()
        );

        String imageId = "654321";
        ImageMongo expectedImage = new ImageMongo();
        expectedImage.setId(imageId);
        expectedImage.setFileName("test.jpg");
        expectedImage.setFormat("image/jpeg");
        expectedImage.setData("some image content".getBytes());

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(imageMongoRepository.findByAssociatedUsername(testUsername)).thenReturn(Optional.of(existingImage));
        when(imageMongoRepository.save(any(ImageMongo.class))).thenReturn(expectedImage);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        ImageMongo result = userService.addUserProfilePicture(file, mockJwtToken);

        // Then
        assertNotNull(result);
        assertNotEquals("0", result.getId());
        assertEquals("test.jpg", result.getFileName());
        verify(jwtService, times(1)).extractUsername(mockJwtToken.substring(7));
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(userRepository, times(1)).save(testUser);
        verify(imageMongoRepository, times(1)).save(any(ImageMongo.class));
    }

    // getUserDP

    @Test
    public void should_not_get_dp_when_no_dp() throws Exception {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(imageMongoRepository.findByAssociatedUsername(testUsername)).thenReturn(Optional.empty());

        // When
        ImageMongo result = userService.getUserProfilePicture(mockJwtToken);

        // Then
        assertNull(result);
        verify(imageMongoRepository, times(1)).findByAssociatedUsername(testUsername);
    }

    @Test
    public void should_get_dp_when_dp_exists() throws Exception {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setUserDPId("123456");
        ImageMongo expectedImage = new ImageMongo();
        expectedImage.setId("123456");
        expectedImage.setFileName("test.jpg");
        expectedImage.setFormat("image/jpeg");
        expectedImage.setData("some image content".getBytes());

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(imageMongoRepository.findByAssociatedUsername(testUsername)).thenReturn(Optional.of(expectedImage));

        // When
        ImageMongo result = userService.getUserProfilePicture(mockJwtToken);

        // Then
        assertNotNull(result);
        assertEquals("123456", result.getId());
        assertEquals("test.jpg", result.getFileName());
        verify(imageMongoRepository, times(1)).findByAssociatedUsername(testUsername);
    }

    // removeUserDP

    @Test
    public void should_not_remove_dp_when_user_has_no_dp() throws Exception {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(imageMongoRepository.findByAssociatedUsername(testUsername)).thenReturn(Optional.empty());

        // When
        int result = userService.removeUserProfilePicture(mockJwtToken);

        // Then
        assertEquals(-1, result);
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(imageMongoRepository, never()).delete(any(ImageMongo.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void should_remove_dp_when_dp_exists() throws Exception {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testUser.setUserDPId("123456");

        ImageMongo expectedImage = new ImageMongo();
        expectedImage.setId("123456");
        expectedImage.setFileName("test.jpg");
        expectedImage.setFormat("image/jpeg");
        expectedImage.setData("some image content".getBytes());

        // Mock
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(imageMongoRepository.findByAssociatedUsername(testUsername)).thenReturn(Optional.of(expectedImage));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        int result = userService.removeUserProfilePicture(mockJwtToken);

        // Then
        assertEquals(1, result);
        assertEquals("", testUser.getUserDPId());
        verify(userRepository, times(1)).findByUsername(testUsername);
        verify(imageMongoRepository, times(1)).delete(expectedImage);
        verify(userRepository, times(1)).save(testUser);
    }

    // changePassword

    @Test
    public void should_not_change_password_when_current_password_is_wrong() {
        // Given
        testUser.setPassword("encodedPassword");

        Authentication authentication = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword");
        request.setConfirmationPassword("newPassword");

        // Mock
        when(passwordEncoder.matches(request.getCurrentPassword(), testUser.getPassword())).thenReturn(false);

        // Then
        assertThrows(IllegalStateException.class, () -> userService.changePassword(request, authentication) , "Wrong Password");
    }

    @Test
    public void should_not_change_password_when_new_and_confirmation_password_mismatches() {
        // Given
        testUser.setPassword("encodedPassword");

        Authentication authentication = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");
        request.setConfirmationPassword("differentPassword");

        // Mock
        when(passwordEncoder.matches(request.getCurrentPassword(), testUser.getPassword())).thenReturn(true);

        // Then
        assertThrows(IllegalStateException.class, () -> userService.changePassword(request, authentication) , "New and Confirmed Passwords are not same");
    }

    @Test
    public void should_change_password() {
        // Given
        testUser.setPassword("encodedPassword");

        Authentication authentication = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");
        request.setConfirmationPassword("newPassword");

        // Mock
        when(passwordEncoder.matches(request.getCurrentPassword(), testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedNewPassword");

        // When
        userService.changePassword(request, authentication);

        // Then
        assertEquals("encodedNewPassword", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }

    // CHAT Based Methods

    // disconnectUser

    @Test
    public void should_disconnect_user_if_exists() {
        // Mock
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        userService.disconnectUser(testUserId);

        // Then
        assertEquals(Status.OFFLINE, testUser.getStatus());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void should_not_disconnect_user_if_not_exists() {
        // Mock
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When
        userService.disconnectUser(testUserId);

        // Then
        verify(userRepository, never()).save(testUser);
    }

    // findConnectedUsers

    @Test
    public void should_get_connected_users() {
        // Given
        testUser.setStatus(Status.ONLINE);
        adminTestUser.setStatus(Status.ONLINE);

        // Mock
        when(userRepository.findAllByStatus(Status.ONLINE)).thenReturn(List.of(testUser, adminTestUser));

        // When
        List<User> responseConnectedUsers = userService.findConnectedUsers();

        // Then
        assertEquals(2, responseConnectedUsers.size());
        assertTrue(responseConnectedUsers.contains(testUser));
        assertFalse(responseConnectedUsers.contains(secondTestUser));
        verify(userRepository, times(1)).findAllByStatus(Status.ONLINE);
    }
}