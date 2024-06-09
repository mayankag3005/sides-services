package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post", uniqueConstraints = { @UniqueConstraint(columnNames = { "postId" }) })
@Entity
@Data
public class Post {

    @Id
    @Column(unique=true)
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

    private String[] hashtags;

    private Long[] likes;

    private Long[] comments;

    private Long[] interestedUsers;

    private Long[] confirmedUsers;


    public void setPostId() {
        this.postId = Long.valueOf(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));
    }

    public void setCreatedTs() {
        this.createdTs = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
    }
}
