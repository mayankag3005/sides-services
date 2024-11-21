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
    @Column(unique=true)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "userId")
//    @JsonBackReference
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

    @ElementCollection
    private List<String> imageIds;  // List of image IDs from MongoDB

    @ElementCollection
    private List<String> videoIds;  // List of video IDs from MongoDB

//    private ArrayList<Gif> gifs;

    private String[] tags;

    private String[] hashtags;

    private String[] likes;

    private Long[] comments;

    @ManyToMany(mappedBy = "requestedPosts")
//    @JsonBackReference
    @JsonIgnore
    private List<User> interestedUsers;

    @ManyToMany(mappedBy = "reminderPosts")
//    @JsonBackReference
    @JsonIgnore
    private List<User> confirmedUsers;
}
