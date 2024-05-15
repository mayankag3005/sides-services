package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
@Entity
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long postId;

    private Long userId;

    private String description;

    private Timestamp createdTs;

    private String postType;

    private String timeType;

    private String postStartTs;

    private String postEndTs;

    private String location;

//    private ArrayList<Image> images;
//
//    private ArrayList<Video> videos;

//    private ArrayList<Tag> tags;
//
//    private ArrayList<Hashtag> hashtags;
//
//    private ArrayList<Like> likes;
//
//    private ArrayList<Comment> comments;
//
//    private ArrayList<Friend> interestedUsers;

    private char onlyForWomen;
}
