package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.Random;

@Table(name = "comment", schema="socialise", uniqueConstraints = { @UniqueConstraint(columnNames = { "commentId" }) })
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @Column(unique=true)
    private Long commentId;

    private Long userId;

    private String description;

    private Long[] commentLikes;

    public void setCommentId() {
        this.commentId = Long.valueOf(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));
    }

//    public Long getCommentId() {
//        return commentId;
//    }

//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public Long[] getCommentLikes() {
//        return commentLikes;
//    }
//
//    public void setCommentLikes(Long[] commentLikes) {
//        this.commentLikes = commentLikes;
//    }
}
