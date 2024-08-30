package com.socialising.services.model.nosql;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ImageMongo {
    @Id
    private String id; // Unique ID for the image

    private byte[] data; // Binary data of the image

    private String fileName;

    private String format; // Image format, e.g., "jpg", "png", "gif"

    private String type; // e.g., "user_picture", "chat_group_picture", "message_content"

    private String associatedUsername; // Optional: user ID if the image is a profile picture

    private String associatedRoomId; // Optional: room ID if the image is a room picture

    private String associatedMessageId; // Optional: message ID if the image is part of a message

    private Date uploadTimestamp; // Time when the image was uploaded

    private long size; // Size of the image in bytes
}
