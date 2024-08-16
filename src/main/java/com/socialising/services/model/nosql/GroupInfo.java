package com.socialising.services.model.nosql;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class GroupInfo {
    @Id
    private String id; // Info ID

    private String roomPicture; // ID of room picture

    private String description;

    private Timestamp createdTimestamp;

    private String adminUser; // Admin username
}
