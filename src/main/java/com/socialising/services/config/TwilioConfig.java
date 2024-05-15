package com.socialising.services.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioConfig {

//    @Bean
//    @ConfigurationProperties(prefix = "twilio", ignoreUnknownFields = false)
//    public TwilioConfig setTwilioConfig() {
//        return new TwilioConfig();
//    }

    private final String ACCOUNT_SID = "";

    private String AUTH_TOKEN = "";

    private String TRIAL_NUMBER = "6387905989";

    public String getTrialNumber() {
        return TRIAL_NUMBER;
    }

    public String getAccountSid() {
        return ACCOUNT_SID;
    }

    public String getAuthToken() {
        return AUTH_TOKEN;
    }
}
