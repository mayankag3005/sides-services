package com.socialising.services.model.chat;

import com.socialising.services.constants.MessageType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessage {

    @Id
    private String id;

    private String chatId;

    private String senderId;

    private String recipientId;

    private String content; // the message

//    @Enumerated(EnumType.STRING)
//    private MessageType type;
    private Date timestamp;
}
