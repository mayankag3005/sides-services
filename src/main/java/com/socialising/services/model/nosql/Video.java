package com.socialising.services.model.nosql;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Video {
    @Id
    private String id; // Unique ID for the video

    private byte[] data; // Binary data of the video

    private String videoName;

    private String format; // Video format, e.g., "mp4", "gif"

    private String type; // e.g., "message_content"

    private String associatedMessageId; // Optional: message ID if the video is part of a message

    private Date uploadTimestamp; // Time when the video was uploaded

    private long size; // Size of the video in bytes
}
