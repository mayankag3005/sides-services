package com.socialising.services.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    private String name;

    private String email;

    private String phoneNumber;

    private String dob;

//    private ArrayList<Friend> friends;
//
//    private ArrayList<Post> posts;
//
//    private ArrayList<ConfirmedPost> reminderBucket;
}
