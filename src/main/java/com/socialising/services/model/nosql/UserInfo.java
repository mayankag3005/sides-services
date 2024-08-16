package com.socialising.services.model.nosql;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class UserInfo {
    @Id
    private String id; // User ID

    private String username;

    private String profilePicture; // URL to profile picture

    private String status; // "online" or "offline"

    private long lastSeenTimestamp;
}
