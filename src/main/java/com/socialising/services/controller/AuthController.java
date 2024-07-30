package com.socialising.services.controller;

import com.socialising.services.model.auth.AuthRequest;
import com.socialising.services.model.auth.AuthenticationRequest;
import com.socialising.services.model.auth.AuthenticationResponse;
import com.socialising.services.model.auth.RegisterRequest;
import com.socialising.services.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth/user/")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthenticationService authenticationService;

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    // Register new user - Sign up
    @PostMapping("register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    // Login with username - password
    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws Exception {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    // Refresh token
    @PostMapping("refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        authenticationService.refreshToken(request, response);
    }

    // Login with Phone number - OTP
    @GetMapping("requestOtp/{phoneNo}")
    public Map<String, Object> getOtp(@PathVariable String phoneNo) {
        return authenticationService.getOtp(phoneNo, "phoneNumber");
    }

    @GetMapping("getOtp/{phoneNumber}")
    public String getCachedOtp(@PathVariable String phoneNumber) {
        return authenticationService.getCachedOtp(phoneNumber);
    }

    @PostMapping("verifyOtp")
    public Map<String, Object> verifyOtp(@RequestBody AuthRequest authenticationRequest) {
        return authenticationService.verifyOtp(authenticationRequest, "phoneNumber");
    }

    // Login with Email - OTP
    @GetMapping("requestEmailOtp/{email}")
    public Map<String, Object> getEmailOtp(@PathVariable String email) {
        return authenticationService.getOtp(email, "email");
    }

    @GetMapping("/getEmailOtp/{email}")
    public String getCachedEmailOtp(@PathVariable String email) {
        return authenticationService.getCachedOtp(email);
    }

    @PostMapping("verifyEmailOtp")
    public Map<String, Object> verifyEmailOtp(@RequestBody AuthRequest authenticationRequest) {
        return authenticationService.verifyOtp(authenticationRequest, "email");
    }

    // Testing
    @GetMapping("/getUsernameFromToken")
    public String getUsernameFromToken(@RequestHeader("Authorization") String token) {
        return authenticationService.getUsernameFromToken(token);
    }

}
