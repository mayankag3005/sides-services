package com.socialising.services.service;

import com.socialising.services.config.TwilioConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class OtpServiceTest {

    @Mock
    private TwilioConfig twilioConfig;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        when(twilioConfig.getTRIAL_NUMBER()).thenReturn("TRIAL_NUMBER");
        when(twilioConfig.getACCOUNT_SID()).thenReturn("ACCOUNT_SID");
        when(twilioConfig.getAUTH_TOKEN()).thenReturn("AUTH_TOKEN");
    }

    // generateOtp

    @Test
    public void should_generate_otp_for_phoneNumber() {
        // Given
        String phoneNumber = "+1234567890";

        // When
        String responseOtp = otpService.generateOtp(phoneNumber);

        // Then
        assertNotNull(responseOtp);
        assertEquals(6, responseOtp.length());
        assertEquals(otpService.getCacheOtp(phoneNumber), responseOtp);
    }

    @Test
    public void should_not_generate_otp_for_empty_phoneNumber() {
        // When
        String responseOtp = otpService.generateOtp("");

        // Then
        assertNotNull(responseOtp);
        assertEquals(0, responseOtp.length());
        assertEquals("", responseOtp);
    }

    // generateOtpForEmail

    @Test
    public void should_generate_otp_for_email() {
        // Given
        String email = "test@example.com";

        // When
        String responseOtp = otpService.generateOtpForEmail(email);

        // Then
        assertNotNull(responseOtp);
        assertEquals(6, responseOtp.length());
        assertEquals(otpService.getCacheOtp(email), responseOtp);
    }

    @Test
    public void should_not_generate_otp_for_empty_email() {
        // When
        String responseOtp = otpService.generateOtpForEmail("");

        // Then
        assertNotNull(responseOtp);
        assertEquals(0, responseOtp.length());
        assertEquals("", responseOtp);
    }

    // getCacheOtp

    @Test
    public void should_get_otp_for_phoneNumber() {
        // Given
        String phoneNumber = "+1234567890";

        // When
        String generateOtp = otpService.generateOtp(phoneNumber);
        String responseOtp = otpService.getCacheOtp(phoneNumber);

        // Then
        assertNotNull(responseOtp);
        assertEquals(6, responseOtp.length());
        assertEquals(generateOtp, responseOtp);
    }

    @Test
    public void should_get_empty_otp_when_not_generated() {
        // Given
        String phoneNumber = "+1234567890";

        // When
        String responseOtp = otpService.getCacheOtp(phoneNumber);

        // Then
        assertNotNull(responseOtp);
        assertEquals(0, responseOtp.length());
        assertEquals("", responseOtp);
    }

//    @Test
//    void test_get_cache_otp_with_expired_otp() throws InterruptedException {
//        String phoneNumber = "+1234567890";
//        otpService.generateOtp(phoneNumber);
//
//        // Wait for OTP to expire (simulate expiry by waiting for more than 5 minutes)
//        Thread.sleep(TimeUnit.MINUTES.toMillis(6));
//
//        String cachedOtp = otpService.getCacheOtp(phoneNumber);
//        assertEquals("", cachedOtp);
//    }

    // clearOtp

    @Test
    public void should_clear_otp_when_generated_before() {
        // Given
        String phoneNumber = "+1234567890";

        // When
        String generateOtp = otpService.generateOtp(phoneNumber);
        otpService.clearOtp(phoneNumber);
        String responseOtp = otpService.getCacheOtp(phoneNumber);

        // Then
        assertNotNull(responseOtp);
        assertEquals(0, responseOtp.length());
        assertEquals("", responseOtp);
    }

    @Test
    public void test_clear_otp_when_not_generated_before() {
        // Given
        String phoneNumber = "+1234567890";

        // When
        otpService.clearOtp(phoneNumber);
        String responseOtp = otpService.getCacheOtp(phoneNumber);

        // Then
        assertNotNull(responseOtp);
        assertEquals(0, responseOtp.length());
        assertEquals("", responseOtp);
    }


}