package com.socialising.services.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    private String currentPassword;

    private String newPassword;

    private String confirmationPassword;
}
