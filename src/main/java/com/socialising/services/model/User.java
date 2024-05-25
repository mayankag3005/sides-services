package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.text.DecimalFormat;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Entity
@Data
public class User {

    public Long getUserId() {
        return userId;
    }

    public void setUserId() {
        this.userId = Long.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
    }

    public String getUsername() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setPhoneNumber(String phoneNo) {
        this.phoneNumber = phoneNo;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    @Id
    private Long userId;

    private String username;

    private String email;

    private String phoneNumber;

    private String dob;

//    private ArrayList<Friend> friends;
//
//    private ArrayList<Post> posts;
//
//    private ArrayList<ConfirmedPost> reminderBucket;
}
