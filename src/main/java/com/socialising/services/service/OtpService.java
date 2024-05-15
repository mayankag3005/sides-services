package com.socialising.services.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.socialising.services.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final Integer EXPIRE_MIN = 5;

    private final LoadingCache<String, String> otpCache;

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

    public String generateOtp(String phoneNo) {
        PhoneNumber to = new PhoneNumber(phoneNo);
        PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
        String otp = getRandomOtp(phoneNo);
        String otpMessage = "Dear Customer, Your OTP is " + otp + ". Use this otp to log in to the Application";

        System.out.println("OTP MESSAGE: " + otpMessage);

        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

        Message message = Message.creator(to, from, otpMessage).create();

        return otp;
    }

    private String getRandomOtp(String phoneNo) {
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));

        otpCache.put(phoneNo, otp);

        return otp;
    }

    public String getCacheOtp(String key) {
        try {
            return otpCache.get(key);
        } catch (Exception e) {
            return "";
        }
    }

    public void clearOtp(String key) {
        otpCache.invalidate(key);
    }

}
