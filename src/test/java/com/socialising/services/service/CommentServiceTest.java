package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.Role;
import com.socialising.services.dto.CommentDTO;
import com.socialising.services.dto.CommentResponseDTO;
import com.socialising.services.mapper.CommentMapper;
import com.socialising.services.mapper.PostMapper;
import com.socialising.services.model.Comment;
import com.socialising.services.model.Post;
import com.socialising.services.model.User;
import com.socialising.services.repository.CommentRepository;
import com.socialising.services.repository.PostRepository;
import com.socialising.services.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private Long testCommentId;
    private Long secondTestCommentId;
    private Long testUserId;
    private Long testPostId;
    private String testUsername;
    private String secondTestUsername;
    private User testUser;
    private User secondTestUser;
    private Post testPost;
    private CommentDTO testCommentDTO;
    private CommentResponseDTO testCommentResponseDTO;
    private CommentResponseDTO secondTestCommentResponseDTO;
    private Comment testComment;
    private Comment secondTestComment;

    @BeforeEach
    public void setUp() {
        testCommentId = 1L;
        secondTestCommentId = 4L;
        testUserId = 2L;
        testPostId = 3L;

        testUsername = "test-username";
        secondTestUsername = "second-test-username";

        testUser = User.builder()
                .userId(testUserId)
                .username(testUsername)
                .email("test@email.com")
                .phoneNumber("1234567890")
                .role(Role.USER)
                .build();

        secondTestUser = User.builder()
                .userId(5L)
                .username(secondTestUsername)
                .email("test@email.com")
                .phoneNumber("1234567890")
                .role(Role.USER)
                .build();

        testPost = Post.builder()
                .postId(testPostId)
                .description("This is test post")
                .postType("general")
                .timeType("later")
                .postStartTs("2024-07-13")
                .postEndTs("2024-08-15")
                .location("Amity")
                .onlyForWomen('N')
                .build();

        testCommentDTO = CommentDTO.builder()
                .description("This is a test comment")
                .build();

        testCommentResponseDTO = CommentResponseDTO.builder()
                .commentId(testCommentId)
                .description("This is a test comment")
                .username(testUsername)
                .build();

        secondTestCommentResponseDTO = CommentResponseDTO.builder()
                .commentId(secondTestCommentId)
                .description("This is second test comment")
                .username(secondTestUsername)
                .build();


        testComment = Comment.builder()
                .commentId(testCommentId)
                .description("This is a test comment")
                .username(testUsername)
                .postId(testPostId)
                .build();

        secondTestComment = Comment.builder()
                .commentId(secondTestCommentId)
                .description("This is second test comment")
                .username(secondTestUsername)
                .postId(testPostId)
                .build();
    }

    // addCommentOnPost

    @Test
    public void should_not_add_comment_when_post_does_not_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());

        // When
        CommentResponseDTO responseCommentDTO = commentService.addCommentOnPost(testPostId, testCommentDTO, mockJwtToken);

        // Then
        assertNull(responseCommentDTO);
        verify(postRepository, never()).save(any(Post.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void should_not_add_comment_when_user_does_not_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn("");

        // When
        CommentResponseDTO responseCommentDTO = commentService.addCommentOnPost(testPostId, testCommentDTO, mockJwtToken);

        // Then
        assertNull(responseCommentDTO);
        verify(postRepository, never()).save(any(Post.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void should_add_comment_on_post() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(commentRepository.save(testComment)).thenReturn(testComment);
        when(postRepository.save(testPost)).thenReturn(testPost);

        // When
        CommentResponseDTO responseCommentDTO = commentService.addCommentOnPost(testPostId, testCommentDTO, mockJwtToken);

        // Then
        assertNotNull(responseCommentDTO);
        assertEquals("This is a test comment", responseCommentDTO.getDescription());
        assertEquals(testUsername, responseCommentDTO.getUsername());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

//    @Test
//    public void test_not_add_comment_with_exception() {
//        // Given
//        String mockJwtToken = "Bearer mock.jwt.token";
//
//        // Mock
//        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
//        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
//        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
//        when(commentRepository.save(testComment)).thenThrow(new RuntimeException("Exception Occurred"));
//
//        // When
//        CommentResponseDTO responseCommentDTO = commentService.addCommentOnPost(testPostId, testCommentDTO, mockJwtToken);
//
//        // Then
//        assertNull(responseCommentDTO);
//        verify(commentRepository, times(1)).save(any(Comment.class));
//        verify(postRepository, never()).save(any(Post.class));
//    }

    // getAllCommentsOnPost

    @Test
    public void should_not_get_comments_when_post_does_not_exist() {
        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());

        // When
        ArrayList<CommentResponseDTO> responseCommentList = commentService.getAllCommentsOnPost(testPostId);

        // Then
        assertNull(responseCommentList);
        verify(postRepository, times(1)).findById(testPostId);
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    public void should_not_get_comments_when_post_has_no_comments() {
        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));

        // When
        ArrayList<CommentResponseDTO> responseCommentList = commentService.getAllCommentsOnPost(testPostId);

        // Then
        assertNull(responseCommentList);
        verify(postRepository, times(2)).findById(testPostId);
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    public void should_get_comments_when_post_has_comments() {
        // Given
        testPost.setComments(new Long[]{testCommentId, secondTestCommentId});

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(commentRepository.findById(secondTestCommentId)).thenReturn(Optional.of(secondTestComment));

        // When
        ArrayList<CommentResponseDTO> responseCommentList = commentService.getAllCommentsOnPost(testPostId);

        // Then
        assertNotNull(responseCommentList);
        assertEquals(2, responseCommentList.size());
        assertEquals("This is a test comment", responseCommentList.get(0).getDescription());
        assertEquals("This is second test comment", responseCommentList.get(1).getDescription());
        verify(postRepository, times(2)).findById(testPostId);
        verify(commentRepository, times(2)).findById(anyLong());
    }

    // deleteCommentOnPost

    @Test
    public void should_not_delete_comment_when_post_does_not_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());

        // When
        int result = commentService.deleteCommentOnPost(testPostId, testCommentId, mockJwtToken);

        // Then
        assertEquals(-1, result);
        verify(postRepository, times(1)).findById(testPostId);
        verify(postRepository, never()).save(any(Post.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void should_not_delete_comment_when_comment_on_post_does_not_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));

        // When
        int result = commentService.deleteCommentOnPost(testPostId, testCommentId, mockJwtToken);

        // Then
        assertEquals(-1, result);
        verify(postRepository, times(2)).findById(testPostId);
        verify(postRepository, never()).save(any(Post.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void should_not_delete_comment_when_user_is_not_authorized() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testPost.setComments(new Long[]{testCommentId});

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(secondTestUsername);
        when(userRepository.findByUsername(secondTestUsername)).thenReturn(Optional.of(secondTestUser));

        // When
        int result = commentService.deleteCommentOnPost(testPostId, testCommentId, mockJwtToken);

        // Then
        assertEquals(-1, result);
        verify(postRepository, times(2)).findById(testPostId);
        verify(commentRepository, times(2)).findById(testCommentId);
        verify(postRepository, never()).save(any(Post.class));
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    public void should_delete_comment_when_user_is_authorized() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testPost.setComments(new Long[]{testCommentId});

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        int result = commentService.deleteCommentOnPost(testPostId, testCommentId, mockJwtToken);

        // Then
        assertEquals(1, result);
        verify(postRepository, times(2)).findById(testPostId);
        verify(commentRepository, times(2)).findById(testCommentId);
        verify(postRepository, times(1)).save(testPost);
        verify(commentRepository, times(1)).deleteById(testCommentId);
    }
    @Test
    public void test_delete_comment_when_exception_occurs() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testPost.setComments(new Long[]{testCommentId});

        // Mock
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Exception occurred")).when(commentRepository).deleteById(testCommentId);

        // When
        int result = commentService.deleteCommentOnPost(testPostId, testCommentId, mockJwtToken);

        // Then
        assertEquals(0, result);
        verify(postRepository, times(2)).findById(testPostId);
        verify(commentRepository, times(2)).findById(testCommentId);
        verify(postRepository, never()).save(testPost);
        verify(commentRepository, times(1)).deleteById(testCommentId);
    }

    // likeAComment

    @Test
    public void should_not_like_a_comment_on_post_when_comment_does_not_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.empty());

        // When
        int result = commentService.likeAComment(testCommentId, mockJwtToken);

        // Then
        assertEquals(-1, result);
        verify(commentRepository, times(1)).findById(testCommentId);
        verify(commentRepository, never()).save(testComment);
    }

    @Test
    public void should_not_like_a_comment_on_post_when_user_has_already_the_comment() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testComment.setCommentLikes(new String[]{testUsername});

        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);

        // When
        int result = commentService.likeAComment(testCommentId, mockJwtToken);

        // Then
        assertEquals(0, result);
        verify(commentRepository, times(2)).findById(testCommentId);
        verify(commentRepository, never()).save(testComment);
    }

    @Test
    public void should_like_a_comment_on_post_when_user_has_not_already_the_comment() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(commentRepository.save(testComment)).thenReturn(testComment);

        // When
        int result = commentService.likeAComment(testCommentId, mockJwtToken);

        // Then
        assertEquals(1, result);
        assertTrue(ArrayUtils.contains(testComment.getCommentLikes(), testUsername));
        verify(commentRepository, times(2)).findById(testCommentId);
        verify(commentRepository, times(1)).save(testComment);
    }

    // getAllLikesOnComment

    @Test
    public void should_not_get_likes_on_a_comment_on_post_when_comment_does_not_exist() {
        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.empty());

        // When
        String[] responseCommentLikes = commentService.getAllLikesOnComment(testCommentId);

        // Then
        assertNull(responseCommentLikes);
        verify(commentRepository, times(1)).findById(testCommentId);
    }

    @Test
    public void should_get_zero_likes_on_a_comment_on_post_when_likes_not_present() {
        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));

        // When
        String[] responseCommentLikes = commentService.getAllLikesOnComment(testCommentId);

        // Then
        assertNotNull(responseCommentLikes);
        assertEquals(0, responseCommentLikes.length);
        assertFalse(ArrayUtils.contains(responseCommentLikes, testUsername));
        verify(commentRepository, times(2)).findById(testCommentId);
    }

    @Test
    public void should_get_likes_on_a_comment_on_post_when_likes_are_present() {
        // Given
        testComment.setCommentLikes(new String[]{testUsername});

        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));

        // When
        String[] responseCommentLikes = commentService.getAllLikesOnComment(testCommentId);

        // Then
        assertNotNull(responseCommentLikes);
        assertEquals(1, responseCommentLikes.length);
        assertTrue(ArrayUtils.contains(responseCommentLikes, testUsername));
        verify(commentRepository, times(2)).findById(testCommentId);
    }

    // removeAlikeOnPost

    @Test
    public void should_not_remove_like_a_comment_on_post_when_comment_does_not_exist() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.empty());

        // When
        int result = commentService.removeAlikeOnPost(testCommentId, mockJwtToken);

        // Then
        assertEquals(-1, result);
        verify(commentRepository, times(1)).findById(testCommentId);
        verify(commentRepository, never()).save(testComment);
    }

    @Test
    public void should_not_remove_like_a_comment_on_post_when_user_has_not_liked_the_comment() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";

        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);

        // When
        int result = commentService.removeAlikeOnPost(testCommentId, mockJwtToken);

        // Then
        assertEquals(0, result);
        verify(commentRepository, times(2)).findById(testCommentId);
        verify(commentRepository, never()).save(testComment);
    }

    @Test
    public void should_remove_like_a_comment_on_post_when_user_has_liked_the_comment() {
        // Given
        String mockJwtToken = "Bearer mock.jwt.token";
        testComment.setCommentLikes(new String[]{testUsername, secondTestUsername});

        // Mock
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(jwtService.extractUsername(mockJwtToken.substring(7))).thenReturn(testUsername);
        when(commentRepository.save(testComment)).thenReturn(testComment);

        // When
        int result = commentService.removeAlikeOnPost(testCommentId, mockJwtToken);

        // Then
        assertEquals(1, result);
        assertEquals(1, testComment.getCommentLikes().length);
        assertTrue(ArrayUtils.contains(testComment.getCommentLikes(), secondTestUsername));
        assertFalse(ArrayUtils.contains(testComment.getCommentLikes(), testUsername));
        verify(commentRepository, times(2)).findById(testCommentId);
        verify(commentRepository, times(1)).save(testComment);
    }

}