package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.Role;
import com.socialising.services.constants.TokenType;
import com.socialising.services.controller.PostController;
import com.socialising.services.model.User;
import com.socialising.services.model.auth.AuthRequest;
import com.socialising.services.model.auth.AuthenticationRequest;
import com.socialising.services.model.auth.AuthenticationResponse;
import com.socialising.services.model.auth.RegisterRequest;
import com.socialising.services.model.token.Token;
import com.socialising.services.repository.TokenRepository;
import com.socialising.services.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final OtpService otpService;

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    public AuthenticationResponse register(RegisterRequest request) {
        Long userId = Long.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
        // create a user object
        var user = User.builder()
                .userId(userId)
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        var savedUser = userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect Username or Password", e);
        }

        // Till this point, user is authenticated, Else error thrown on above authenticate method of AuthenticationManager
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        // Revoke all the existing user tokens
        revokeAllUserTokens(user);

        // Save jwt token in DB for each user
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public Map<String, Object> getOtp(String phoneNoOrEmail, String type) {
        Map<String, Object> returnMap = new HashMap<>();

        try {
            String otp = type.equals("email") ? otpService.generateOtpForEmail(phoneNoOrEmail) : otpService.generateOtp(phoneNoOrEmail);
            returnMap.put("otp", otp);
            returnMap.put("status", "success");
            returnMap.put("message", "OTP Sent Successfully");
        } catch (Exception e) {
            returnMap.put("status", "failed");
            returnMap.put("message", e.getMessage());
        }

        return returnMap;
    }

    public String getCachedOtp(String phoneNumber) {
        return otpService.getCacheOtp(phoneNumber);
    }

    public Map<String, Object> verifyOtp(AuthRequest authRequest, String type) {
        Map<String, Object> returnMap = new HashMap<>();

        try {
            String phoneNumberOrEmail = authRequest.getPhoneNumberOrEmail();
            String authOtp = authRequest.getOtp();
            log.info("{} has Auth OTP: {}", phoneNumberOrEmail, authOtp);

            String cachedOtp = otpService.getCacheOtp(phoneNumberOrEmail);
            log.info("{} has Cached OTP: {}", phoneNumberOrEmail, cachedOtp);

            if(authOtp.equals(cachedOtp)) {

                String jwtToken = createAuthenticationToken(authRequest, type);

                returnMap.put("status", "success");
                returnMap.put("message", "OTP Verified Successfully");
                returnMap.put("jwt", jwtToken);
                otpService.clearOtp(authRequest.getPhoneNumberOrEmail());
            } else {
                returnMap.put("status", "success");
                returnMap.put("message", "OTP is either expired or incorrect");
            }
        } catch (Exception e) {
            returnMap.put("status", "failed");
            returnMap.put("message", e.getMessage());
        }

        return returnMap;
    }

    public String createAuthenticationToken(AuthRequest authRequest, String type) throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getPhoneNumber(), authRequest.getOtp()));
//        } catch (BadCredentialsException e) {
//            throw new Exception("Incorrect Phone Number or otp", e);
//        }
        try {
            final UserDetails userDetails = type.equals("phoneNumber") ?
                    userRepository
                            .findByPhoneNumber(authRequest.getPhoneNumberOrEmail())
                            .orElseThrow(() -> new UsernameNotFoundException("User not found")) :
                    userRepository
                            .findByEmail(authRequest.getPhoneNumberOrEmail())
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                return jwtService.generateToken(userDetails);
        } catch (Exception e) {
            log.info("User with [{}] not found. Please register user or try Login with username-password", authRequest.getPhoneNumberOrEmail());
            log.info(e.getMessage());
            return null;
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        Integer tokenId = Integer.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
        var token = Token.builder()
                .id(tokenId)
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.Bearer)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokens(user.getUserId());

        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }
}
