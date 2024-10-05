package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.Random;

@Table(name = "comment", schema="socialise", uniqueConstraints = { @UniqueConstraint(columnNames = { "commentId" }) })
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @Column(unique=true)
    private Long commentId;

    private String username;

    private Long postId;

    private String description;

    private String[] commentLikes;
}
