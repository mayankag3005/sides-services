package com.socialising.services.model.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom {

    @Id
    private String id;

    private String chatId;

    private String senderId;

    private String recipientId;

}
