package com.socialising.services.controller;

import com.socialising.services.model.chat.ChatMessage;
import com.socialising.services.model.chat.ChatNotification;
import com.socialising.services.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMessage = chatMessageService.saveMessage(chatMessage);

        // john/queue/messages for john to recieve messages
        messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages",
                ChatNotification.builder()
                        .id(savedMessage.getId())
                        .senderId(savedMessage.getSenderId())
                        .recipientId(savedMessage.getRecipientId())
                        .content(savedMessage.getContent())
                        .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("senderId") String senderId, @PathVariable("recipientId") String recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    // to add a new user and inform the existing users, that a new user is added / joined the chat
//    @MessageMapping("/chat.addUser")
//    @SendTo("/topic/public")
//    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//
//        // Add username in WebSocket session
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        return chatMessage;
//    }
//
//    // send message to a topic
//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/public")
//    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
//        return chatMessage;
//    }
}
