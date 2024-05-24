package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    private String userName;

    private String email;

    private String phoneNumber;

    private String dob;

    public void setPhoneNumber(String phoneNo) {
        this.phoneNumber = phoneNo;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

//    private ArrayList<Friend> friends;
//
//    private ArrayList<Post> posts;
//
//    private ArrayList<ConfirmedPost> reminderBucket;
}
