package com.socialising.services.controller;

import com.socialising.services.model.AuthRequest;
import com.socialising.services.service.OtpService;
import com.socialising.services.service.UserDetailsService;
import com.socialising.services.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/user/")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OtpService otpService;

    @GetMapping("requestOtp/{phoneNo}")
    public Map<String, Object> getOtp(@PathVariable String phoneNo) {
        Map<String, Object> returnMap = new HashMap<>();

        try {
            String otp = otpService.generateOtp(phoneNo);
            returnMap.put("otp", otp);
            returnMap.put("status", "success");
            returnMap.put("message", "OTP Sent Successfully");
        } catch (Exception e) {
            returnMap.put("status", "failed");
            returnMap.put("message", e.getMessage());
        }

        return returnMap;
    }

    @PostMapping("verifyOtp/")
    public Map<String, Object> verifyOtp(@RequestBody AuthRequest authenticationRequest) {
        Map<String, Object> returnMap = new HashMap<>();

        try {
            if(authenticationRequest.getOtp().equals(otpService.getCacheOtp(authenticationRequest.getPhoneNo()))) {
                String jwtToken = createAuthenticationToken(authenticationRequest);

                returnMap.put("status", "success");
                returnMap.put("message", "OTP Verified Successfully");
                returnMap.put("jwt", jwtToken);
                otpService.clearOtp(authenticationRequest.getPhoneNo());
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

    public String createAuthenticationToken(AuthRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getPhoneNo(), ""));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect Username or Password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getPhoneNo());

        return jwtTokenUtil.generateToken(userDetails);
    }
}
