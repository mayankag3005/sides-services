package com.socialising.services.service;

import com.socialising.services.model.token.Token;
import com.socialising.services.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutService logoutService;

    // logout

    @Test
    public void test_logout_with_valid_token() {
        // Given
        String validToken = "Bearer valid.token.test";
        Token storedToken = Token.builder()
                .token("valid.token.test")
                .build();

        // Mock
        when(request.getHeader("Authorization")).thenReturn(validToken);
        when(tokenRepository.findByToken("valid.token.test")).thenReturn(Optional.of(storedToken));

        // When
        logoutService.logout(request, response, authentication);

        // Then
        assertTrue(storedToken.isExpired());
        assertTrue(storedToken.isRevoked());
        verify(tokenRepository, times(1)).save(storedToken);
        verify(tokenRepository, times(1)).findByToken("valid.token.test");
    }

    @Test
    public void test_logout_with_invalid_token() {
        // Given
        String invalidToken = "Bearer invalid.token.test";

        // Mock
        when(request.getHeader("Authorization")).thenReturn(invalidToken);
        when(tokenRepository.findByToken("valid.token.test")).thenReturn(Optional.empty());

        // When
        logoutService.logout(request, response, authentication);

        // Then
        verify(tokenRepository, never()).save(any(Token.class));
        verify(tokenRepository, times(1)).findByToken("invalid.token.test");
    }

    @Test
    public void test_logout_with_no_authorization_header() {
        // Mock
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        logoutService.logout(request, response, authentication);

        // Then
        verify(tokenRepository, never()).save(any(Token.class));
        verify(tokenRepository, never()).findByToken(anyString());
    }

    @Test
    public void test_logout_with_no_bearer_token() {
        // Given
        String invalidToken = "invalid.token.test";

        // Mock
        when(request.getHeader("Authorization")).thenReturn(invalidToken);

        // When
        logoutService.logout(request, response, authentication);

        // Then
        verify(tokenRepository, never()).save(any(Token.class));
        verify(tokenRepository, never()).findByToken(anyString());
    }

}