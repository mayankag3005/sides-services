package com.socialising.services.model.nosql;

import com.socialising.services.constants.ChatType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Chat {

    @Id
    private String id; // Room ID

    private String roomName; // Room name

    private List<String> participants; // User IDs

    private ChatType type; // "1-1" or "group"

    private String infoId;  // Refers to GroupInfo if type is "group"

    private String lastMessageContent;
    private String lastMessageSenderId;
    private Date lastMessageTimestamp;
}
