package com.socialising.services.model.nosql;

import com.socialising.services.constants.MessageStatus;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatMsg {
    @Id
    private String id; // Message ID

    private String roomId;

    private String senderId;

    private String content; // Text

    private String imageContentId; // Image

    private String videoContentId; // Video

    private Date timestamp;

    private MessageStatus status; // "sent", "delivered", "seen"
}
