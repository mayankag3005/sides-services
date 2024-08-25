package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.Role;
import com.socialising.services.model.User;
import com.socialising.services.model.auth.AuthRequest;
import com.socialising.services.model.auth.AuthenticationRequest;
import com.socialising.services.model.auth.AuthenticationResponse;
import com.socialising.services.model.auth.RegisterRequest;
import com.socialising.services.model.token.Token;
import com.socialising.services.repository.TokenRepository;
import com.socialising.services.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private OtpService otpService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    // register

    @Test
    public void should_register_new_user() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setUsername("johnDoe");
        request.setEmail("johndoe@example.com");
        request.setPhoneNumber("1234567890");
        request.setPassword("password");
        request.setRole(Role.USER);

        Long userId = 123456L;

        User user = User.builder()
                .userId(userId)
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password("encodedPassword")
                .role(request.getRole())
                .build();

        // Mock
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        // When
        AuthenticationResponse response = authenticationService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
        verify(passwordEncoder, times(1)).encode(request.getPassword());
    }

    // authenticate

    @Test
    public void should_authenticate_user_with_correct_credentials() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("johnDoe");
        request.setPassword("password");

        User user = new User();
        user.setUserId(123L);
        user.setUsername("johnDoe");

        // Mock
        when(userRepository.findByUsername("johnDoe")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");
        when(tokenRepository.findAllValidTokens(user.getUserId())).thenReturn(List.of());

        // When
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername("johnDoe");
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
        verify(tokenRepository, times(1)).findAllValidTokens(user.getUserId());
        verify(tokenRepository, never()).saveAll(anyList());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    public void should_authenticate_user_with_correct_credentials_and_revoke_previous_tokens() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("johnDoe");
        request.setPassword("password");

        User user = new User();
        user.setUserId(123L);
        user.setUsername("johnDoe");

        Token token1 = new Token();
        token1.setExpired(false);
        token1.setRevoked(false);

        Token token2 = new Token();
        token2.setExpired(false);
        token2.setRevoked(false);

        List<Token> tokens = List.of(token1, token2);

        // Mock
        when(userRepository.findByUsername("johnDoe")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");
        when(tokenRepository.findAllValidTokens(user.getUserId())).thenReturn(tokens);

        // When
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertTrue(token1.isExpired());
        assertTrue(token1.isRevoked());
        assertTrue(token2.isExpired());
        assertTrue(token2.isRevoked());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername("johnDoe");
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
        verify(tokenRepository, times(1)).findAllValidTokens(user.getUserId());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    public void should_not_authenticate_user_with_incorrect_credentials() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("johnDoe");
        request.setPassword("wrongPassword");

        // Mock
        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // When
        assertThrows(Exception.class, () -> authenticationService.authenticate(request), "Incorrect Username or Password");

        // Then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByUsername("johnDoe");
        verify(jwtService, never()).generateToken(any(User.class));
        verify(jwtService, never()).generateRefreshToken(any(User.class));
        verify(tokenRepository, never()).findAllValidTokens(anyLong());
        verify(tokenRepository, never()).save(any(Token.class));
    }

    // refreshToken

    @Test
    public void should_refresh_the_access_token() throws IOException {
        // Given
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        String refreshToken = "Bearer refresh-token";
        String extractedToken = refreshToken.substring(7);
        String username = "test_user";
        User user = new User();
        user.setUsername(username);
        String newAccessToken = "new-access-token";

        request.addHeader(HttpHeaders.AUTHORIZATION, refreshToken);

        // Mock
        when(jwtService.extractUsername(extractedToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(extractedToken, user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(newAccessToken);

        // When
        authenticationService.refreshToken(request, response);
        AuthenticationResponse authResponse = new ObjectMapper().readValue(response.getContentAsString(), AuthenticationResponse.class);

        // Then
        verify(jwtService, times(1)).extractUsername(extractedToken);
        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, times(1)).isTokenValid(extractedToken, user);
        verify(jwtService, times(1)).generateToken(user);

        assertEquals(newAccessToken, authResponse.getAccessToken());
        assertEquals(extractedToken, authResponse.getRefreshToken());
    }

    @Test
    public void should_not_refresh_the_access_token_when_auth_header_null() throws IOException {
        // Given
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // When
        authenticationService.refreshToken(request, response);

        // Then
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    public void should_not_refresh_the_access_token_when_auth_header_invalid() throws IOException {
        // Given
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.addHeader(HttpHeaders.AUTHORIZATION, "InvalidHeader");

        // When
        authenticationService.refreshToken(request, response);

        // Then
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    public void should_not_refresh_the_access_token_when_username_null() throws IOException {
        // Given
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        String refreshToken = "Bearer refresh-token";
        String extractedToken = refreshToken.substring(7);

        request.addHeader(HttpHeaders.AUTHORIZATION, refreshToken);

        // Mock
        when(jwtService.extractUsername(extractedToken)).thenReturn(null);

        // When
        authenticationService.refreshToken(request, response);

        // Then
        verify(jwtService, times(1)).extractUsername(extractedToken);
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    public void should_not_refresh_the_access_token_when_user_not_found() throws IOException {
        // Given
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        String refreshToken = "Bearer refresh-token";
        String extractedToken = refreshToken.substring(7);
        String username = "test_user";

        request.addHeader(HttpHeaders.AUTHORIZATION, refreshToken);

        // Mock
        when(jwtService.extractUsername(extractedToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        authenticationService.refreshToken(request, response);

        // Then
        verify(jwtService, times(1)).extractUsername(extractedToken);
        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, never()).isTokenValid(anyString(), any());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    public void should_not_refresh_the_access_token_when_invalid_token() throws IOException {
        // Given
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        String refreshToken = "Bearer refresh-token";
        String extractedToken = refreshToken.substring(7);
        String username = "test_user";
        User user = new User();
        user.setUsername(username);

        request.addHeader(HttpHeaders.AUTHORIZATION, refreshToken);

        // Mock
        when(jwtService.extractUsername(extractedToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(extractedToken, user)).thenReturn(false);

        // When
        authenticationService.refreshToken(request, response);

        // Then
        verify(jwtService, times(1)).extractUsername(extractedToken);
        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, times(1)).isTokenValid(extractedToken, user);
        verify(jwtService, never()).generateToken(user);
    }

    // getOtp

    @Test
    public void should_get_otp_for_email() {
        // Given
        String email = "test@example.com";
        String type = "email";
        String generatedOtp = "123456";

        // Mock
        when(otpService.generateOtpForEmail(email)).thenReturn(generatedOtp);

        // When
        Map<String, Object> result = authenticationService.getOtp(email, type);

        // Then
        assertEquals("success", result.get("status"));
        assertEquals(generatedOtp, result.get("otp"));
        assertEquals("OTP Sent Successfully", result.get("message"));
        verify(otpService, times(1)).generateOtpForEmail(email);
        verify(otpService, never()).generateOtp(anyString());
    }
    @Test
    public void should_not_get_otp_for_email() {
        // Given
        String email = "test@example.com";
        String type = "email";
        String errorMessage = "Error generating OTP";

        // Mock
        when(otpService.generateOtpForEmail(email)).thenThrow(new RuntimeException(errorMessage));

        // When
        Map<String, Object> result = authenticationService.getOtp(email, type);

        // Then
        assertEquals("failed", result.get("status"));
        assertEquals(errorMessage, result.get("message"));
        verify(otpService, times(1)).generateOtpForEmail(email);
        verify(otpService, never()).generateOtp(anyString());
    }


    @Test
    public void should_get_otp_for_phone_number() {
        // Given
        String phone = "1234567890";
        String type = "phone";
        String generatedOtp = "123456";

        // Mock
        when(otpService.generateOtp(phone)).thenReturn(generatedOtp);

        // When
        Map<String, Object> result = authenticationService.getOtp(phone, type);

        // Then
        assertEquals("success", result.get("status"));
        assertEquals(generatedOtp, result.get("otp"));
        assertEquals("OTP Sent Successfully", result.get("message"));
        verify(otpService, times(1)).generateOtp(phone);
        verify(otpService, never()).generateOtpForEmail(anyString());
    }

    @Test
    public void should_not_get_otp_for_phone_number()  {
        // Given
        String phone = "1234567890";
        String type = "phone";
        String errorMessage = "Error generating OTP";

        // Mock
        when(otpService.generateOtp(phone)).thenThrow(new RuntimeException(errorMessage));

        // When
        Map<String, Object> result = authenticationService.getOtp(phone, type);

        // Then
        assertEquals("failed", result.get("status"));
        assertEquals(errorMessage, result.get("message"));
        verify(otpService, times(1)).generateOtp(phone);
        verify(otpService, never()).generateOtpForEmail(anyString());
    }

    // getCachedOtp

    @Test
    public void should_get_cached_otp() {
        // Given
        String phoneNumber = "1234567890";
        String expectedOtp = "123456";

        // Mock
        when(otpService.getCacheOtp(phoneNumber)).thenReturn(expectedOtp);

        // When
        String responseOtp = authenticationService.getCachedOtp(phoneNumber);

        // Then
        assertEquals(expectedOtp, responseOtp);
        verify(otpService, times(1)).getCacheOtp(phoneNumber);
    }

    @Test
    public void should_not_get_cached_otp_when_not_generated() {
        // Given
        String phoneNumber = "1234567890";

        // Mock
        when(otpService.getCacheOtp(phoneNumber)).thenReturn("");

        // When
        String responseOtp = authenticationService.getCachedOtp(phoneNumber);

        // Then
        assertEquals("", responseOtp);
        verify(otpService, times(1)).getCacheOtp(phoneNumber);
    }

    // verifyOtp

    @Test
    public void should_verify_otp_successfully()throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setPhoneNumberOrEmail("test@example.com");
        authRequest.setOtp("123456");
        String type = "email";

        User user = new User();
        user.setUsername("testUser");

        // Mock
        when(otpService.getCacheOtp(anyString())).thenReturn("123456");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // When
        Map<String, Object> result = authenticationService.verifyOtp(authRequest, type);

        // Then
        assertEquals("success", result.get("status"));
        assertEquals("OTP Verified Successfully", result.get("message"));
        assertEquals("jwtToken", result.get("jwt"));
        verify(otpService, times(1)).getCacheOtp(anyString());
        verify(otpService, times(1)).clearOtp(anyString());
    }

    @Test
    public void should_not_verify_with_wrong_otp()throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setPhoneNumberOrEmail("test@example.com");
        authRequest.setOtp("123456");
        String type = "email";

        // Mock
        when(otpService.getCacheOtp(anyString())).thenReturn("654321");

        // When
        Map<String, Object> result = authenticationService.verifyOtp(authRequest, type);

        // Then
        assertEquals("success", result.get("status"));
        assertEquals("OTP is either expired or incorrect", result.get("message"));
        assertNull(result.get("jwt"));
        verify(otpService, times(1)).getCacheOtp(anyString());
        verify(otpService, never()).clearOtp(anyString());
    }

    @Test
    public void should_not_verify_due_to_exception()throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setPhoneNumberOrEmail("test@example.com");
        authRequest.setOtp("123456");
        String type = "email";

        // Mock
        when(otpService.getCacheOtp(anyString())).thenThrow(new RuntimeException("Error fetching OTP"));

        // When
        Map<String, Object> result = authenticationService.verifyOtp(authRequest, type);

        // Then
        assertEquals("failed", result.get("status"));
        assertEquals("Error fetching OTP", result.get("message"));
        assertNull(result.get("jwt"));
        verify(otpService, times(1)).getCacheOtp(anyString());
        verify(otpService, never()).clearOtp(anyString());
    }

    // createAuthenticationToken

    @Test
    public void test_create_auth_token_with_exception() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setPhoneNumberOrEmail("test@example.com");
        authRequest.setOtp("123456");
        String type = "email";

        // Mock
        when(userRepository.findByEmail("test@example.com")).thenThrow(new RuntimeException("Error fetching user data"));

        // When
        String responseToken = authenticationService.createAuthenticationToken(authRequest, type);

        // Then
        assertNull(responseToken);
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    public void test_create_auth_token_for_user_phoneNumber() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setPhoneNumberOrEmail("1234567890");
        authRequest.setOtp("123456");
        String type = "phoneNumber";

        User user = new User();
        user.setUsername("test_user");

        // Mock
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // When
        String responseToken = authenticationService.createAuthenticationToken(authRequest, type);

        // Then
        assertNotNull(responseToken);
        assertEquals("jwtToken", responseToken);
        verify(userRepository, times(1)).findByPhoneNumber(anyString());
        verify(userRepository, never()).findByEmail(anyString());
    }

    // getUsernameFromToken

    @Test
    void testGetUsernameFromToken_ValidUsername() {
        String token = "Bearer mock.valid.token";
        String expectedUsername = "validUser";

        when(jwtService.extractUsername("mock.valid.token")).thenReturn(expectedUsername);

        String result = authenticationService.getUsernameFromToken(token);

        assertEquals(expectedUsername, result);
        verify(jwtService, times(1)).extractUsername("mock.valid.token");
    }

    @Test
    void testGetUsernameFromToken_NullUsername() {
        String token = "Bearer invalidToken";

        when(jwtService.extractUsername("invalidToken")).thenReturn(null);

        String result = authenticationService.getUsernameFromToken(token);

        assertEquals("", result);
        verify(jwtService, times(1)).extractUsername("invalidToken");
    }
}