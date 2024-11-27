package com.socialising.services.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long postId;
    private String username;
    private String description;
    private Timestamp createdTs;
    private String postType;
    private String timeType;
    private String postStartTs;
    private String postEndTs;
    private String location;
    private boolean onlyForWomen;
    private String[] tags;
    private String[] hashtags;
}

