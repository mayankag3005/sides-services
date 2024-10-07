package com.socialising.services.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long commentId;

    private String username;

    private String description;

    private String[] commentLikes;
}
