package com.socialising.services.model.auth;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest implements Serializable {

    private String otp;

    private String phoneNumberOrEmail;
}
