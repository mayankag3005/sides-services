package com.socialising.services.controller;

import com.socialising.services.dto.ChatMsgDTO;
import com.socialising.services.dto.ChatMsgPrivateDTO;
import com.socialising.services.dto.ChatPrivateDTO;
import com.socialising.services.model.chat.ChatMessage;
import com.socialising.services.model.nosql.ChatMsg;
import com.socialising.services.service.MessageMongoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageMongoController {

    private final MessageMongoService messageMongoService;

    @PostMapping("/room/sendMessage")
    public int sendMessage(ChatMsgDTO chatMsgDTO, @RequestHeader("Authorization") String token) throws IOException {
        return messageMongoService.saveMessage(chatMsgDTO, token);
    }

    @GetMapping("/room/getMessages/{roomId}")
    public List<ChatMsg> getAllMessagesOfRoom(@PathVariable String roomId) {
        return messageMongoService.getAllMessages(roomId);
    }

    @PostMapping("/private/sendMessage")
    public int sendPrivateMessage(@RequestBody ChatMsgPrivateDTO chatMsgPrivateDTO) throws IOException {
        return messageMongoService.sendPrivateMessage(chatMsgPrivateDTO);
    }

    @GetMapping("/private/getMessages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMsg>> findChatMessages(@PathVariable("senderId") String senderId, @PathVariable("recipientId") String recipientId) {
        return ResponseEntity.ok(messageMongoService.findChatMessages(senderId, recipientId));
    }
}