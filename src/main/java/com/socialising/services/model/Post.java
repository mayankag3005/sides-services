package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
@Entity
@Data
public class Post {

    public Post() {}

    public Post(Long userId, String description, Timestamp createdTs, String postType, String timeType, String postStartTs, String postEndTs, String location, char onlyForWomen) {
        this.description = description;
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

    private char onlyForWomen;

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



    public Long getPostId() {
        return postId;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getCreatedTs() {
        return createdTs;
    }

    public String getPostType() {
        return postType;
    }

    public String getTimeType() {
        return timeType;
    }

    public String getPostStartTs() {
        return postStartTs;
    }

    public String getPostEndTs() {
        return postEndTs;
    }

    public String getLocation() {
        return location;
    }

    public char getOnlyForWomen() {
        return onlyForWomen;
    }

    public Long getUserId() {
        return userId;
    }

    public void setPostId() {
        this.postId = Long.valueOf(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));
    }
}
