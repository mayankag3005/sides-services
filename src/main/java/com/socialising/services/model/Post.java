package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
@Entity
@Data
public class Post {

    public Post() {}

    public Post(Long postId, Long userId, String description, Timestamp createdTs, String postType, String timeType, String postStartTs, String postEndTs, String location, char onlyForWomen) {
        this.description = description;
        this.postId = postId;
        this.userId = userId;
        this.createdTs = createdTs;
        this.postType = postType;
        this.timeType = timeType;
        this.postStartTs = postStartTs;
        this.postEndTs = postEndTs;
        this.location = location;
        this.onlyForWomen = onlyForWomen;
    }

    @Id
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
