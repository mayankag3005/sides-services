package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
@Entity
@Data
public class Post {

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

//    private ArrayList<Video> videos;

//    private ArrayList<Gif> gifs;

    private String[] tags;

//    private ArrayList<Hashtag> hashtags;

//    private ArrayList<Like> likes;

//    private ArrayList<Comment> comments;

    private long[] interestedUsers;

    public Long getPostId() {
        return postId;
    }

    public void setPostId() {
        this.postId = Long.valueOf(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs() {
        this.createdTs = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
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

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public long[] getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(long[] interestedUsers) {
        this.interestedUsers = interestedUsers;
    }
}
