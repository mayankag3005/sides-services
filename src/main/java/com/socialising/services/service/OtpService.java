package com.socialising.services.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.socialising.services.config.TwilioConfig;
import com.socialising.services.controller.PostController;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final Integer EXPIRE_MIN = 5;

    private final LoadingCache<String, String> otpCache;

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private TwilioConfig twilioConfig;

    public OtpService() {
        otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES).build(new CacheLoader<>() {
            @Override
            public String load(String key) throws Exception {
                return "";
            }
        });
    }

    public String generateOtp(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            log.info("Please provide a valid Phone Number");
            return "";
        }

        PhoneNumber to = new PhoneNumber(phoneNumber);
        PhoneNumber from = new PhoneNumber(twilioConfig.getTRIAL_NUMBER());
        String otp = getRandomOtp(phoneNumber);
        String otpMessage = "Dear Customer, Your OTP is " + otp + ". Use this otp to log in to the Application";

        log.info("To Phone Number: {} and From Phone Number: {}", to, from);
        System.out.println("OTP MESSAGE: " + otpMessage);

        // SEND OTP to User Phone
//        Twilio.init(twilioConfig.getACCOUNT_SID(), twilioConfig.getAUTH_TOKEN());
//        Message message = Message.creator(to, from, otpMessage).create();

        return otp;
    }

    public String generateOtpForEmail(String email) {
        if (email.isEmpty()) {
            log.info("Please provide a valid Email");
            return "";
        }

        String otp = getRandomOtp(email);
        String otpMessage = "Dear Customer, Your OTP is " + otp + ". Use this otp to log in to the Application";
        System.out.println("OTP MESSAGE: " + otpMessage);

        // Send OTP to User Email

        return otp;
    }

    private String getRandomOtp(String phoneNumberOrEmail) {
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));

        otpCache.put(phoneNumberOrEmail, otp);
        try {
            log.info("OTP: {}, stored in Cache: {}", otp, otpCache.get(phoneNumberOrEmail));
        } catch (Exception e) {
            log.info("Error while getting Cached OTP: {}", e.getMessage());
        }

        return otp;
    }

    public String getCacheOtp(String key) {
        try {
            return otpCache.get(key);
        } catch (Exception e) {
            log.info("OTP Expired or Incorrect Key: {}, Please generate again!!", key);
            log.info("Error in get Cached OTP: {}", e.getMessage());
            return "";
        }
    }

    public void clearOtp(String key) {
        otpCache.invalidate(key);
    }

}
