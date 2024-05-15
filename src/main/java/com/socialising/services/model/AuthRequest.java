package com.socialising.services.model;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest implements Serializable {

    private String otp;

    private String phoneNo;

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getOtp() {
        return otp;
    }
}
