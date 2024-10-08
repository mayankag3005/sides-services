package com.socialising.services.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post", uniqueConstraints = { @UniqueConstraint(columnNames = { "postId" }) })
@Entity
@Builder
public class Post {

    @Id
    @Column(unique=true, nullable=false)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable=false)
    @JsonIgnore
    private User ownerUser;

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

    private String[] likes;

    private Long[] comments;

    @ManyToMany(mappedBy = "requestedPosts")
    @JsonIgnore
    private List<User> interestedUsers;

    @ManyToMany(mappedBy = "reminderPosts")
    @JsonIgnore
    private List<User> confirmedUsers;
}
