package com.socialising.services.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String phoneNumber;

    private String firstName;
    private String lastName;

    private String dob;
    private Integer age;

    private String gender;
    private String religion;
    private String maritalStatus;

    private String city;
    private String state;

    private String homeCity;
    private String homeState;

    private String country;

    private String education;
    private String occupation;

    private List<String> tags;

}