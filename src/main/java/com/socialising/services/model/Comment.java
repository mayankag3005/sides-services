package com.socialising.services.model;

import jakarta.persistence.Id;

public class Comment {

    @Id
    private Long commentId;

    private String description;

    private Long userId;
}
