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
