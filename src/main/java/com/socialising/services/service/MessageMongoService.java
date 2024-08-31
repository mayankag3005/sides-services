package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.MessageStatus;
import com.socialising.services.dto.ChatMsgDTO;
import com.socialising.services.dto.ChatMsgPrivateDTO;
import com.socialising.services.dto.ChatPrivateDTO;
import com.socialising.services.model.User;
import com.socialising.services.model.nosql.Chat;
import com.socialising.services.model.nosql.ChatMsg;
import com.socialising.services.model.nosql.ImageMongo;
import com.socialising.services.model.nosql.Video;
import com.socialising.services.repository.nosql.*;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageMongoService {

    private final ChatRepository chatRepository;

    private final ChatMsgRepository chatMsgRepository;

    private final ChatMongoService chatMongoService;

    private final SimpMessagingTemplate messagingTemplate;

    private final ImageMongoRepository imageMongoRepository;

    private final VideoRepository videoMongoRepository;

    private final JwtService jwtService;

    private String getUsernameFromToken(String token) {
        try {
            String username = jwtService.extractUsername(token.substring(7));
            log.info("Requested User is [{}]", username);
            return username;
        } catch (ExpiredJwtException e) {
            log.info("The token has expired. Please Login again!!");
            return "";
        } catch (Exception e) {
            log.info("Error fetching username from token: {}", e.getMessage());
            return "";
        }
    }

    private boolean checkIfRoomExists(String id) {
        if (chatRepository.findById(id).isPresent()) {
            log.info("Chat Room exists");
            return true;
        }
        log.info("Chat Room does not exists");
        return false;
    }

    public int saveMessage(ChatMsgDTO chatMsgDTO, String token) throws IOException {
        if(!checkIfRoomExists(chatMsgDTO.getRoomId())) {
            return -1;
        }

        String senderUsername = getUsernameFromToken(token);

        // Create new message
        ChatMsg chatMsg = ChatMsg.builder()
                .id(new DecimalFormat("00000000").format(new Random().nextInt(99999999)))
                .roomId(chatMsgDTO.getRoomId())
                .senderId(senderUsername)
                .content(chatMsgDTO.getContent())
                .timestamp(new Date(System.currentTimeMillis()))
                .status(MessageStatus.SENT)
                .build();

        // Check if image is present in message content
        if (chatMsgDTO.getImageContent() != null && !chatMsgDTO.getImageContent().isEmpty()) {
            String imageId = saveImage(chatMsgDTO.getImageContent(), chatMsg.getId());
            chatMsg.setImageContentId(imageId);
        }

        // Check if video is present in message content
        if (chatMsgDTO.getVideoContent() != null && !chatMsgDTO.getVideoContent().isEmpty()) {
            String videoId = saveVideo(chatMsgDTO.getVideoContent(), chatMsg.getId());
            chatMsg.setVideoContentId(videoId);
        }

        // Save New Message to DB
        chatMsgRepository.save(chatMsg);

        return 1;
    }

    public List<ChatMsg> getAllMessages(String roomId) {
        if(!checkIfRoomExists(roomId)) {
            return null;
        }
        return chatMsgRepository.findByRoomId(roomId);
    }

    public int sendPrivateMessage(ChatMsgPrivateDTO chatMsgPrivateDTO) throws IOException {
        String chatId = chatMsgPrivateDTO.getSenderName() + "_" + chatMsgPrivateDTO.getRecipientName();

        // Create new private chat, if not exists
        if (!checkIfRoomExists(chatId)) {
            // Check if chat with opposite chatId exists
            String secondaryChatId = chatMsgPrivateDTO.getRecipientName() + "_" + chatMsgPrivateDTO.getSenderName();
            if(checkIfRoomExists(secondaryChatId)) {
                log.info("Private exists with chat id: [{}]", secondaryChatId);
                chatId = secondaryChatId;
            } else {
                // No chat exists. Creating new chat
                ChatPrivateDTO chatPrivateDTO = new ChatPrivateDTO();
                chatPrivateDTO.setSenderName(chatMsgPrivateDTO.getSenderName());
                chatPrivateDTO.setRecipientName(chatMsgPrivateDTO.getRecipientName());
                Chat privateChat = chatMongoService.createPrivateChat(chatPrivateDTO);

                chatId = privateChat.getId();
                log.info("New chat creating while sending new message: [{}]", chatId);
            }
        } else {
            log.info("Private exists with chat Id: [{}]", chatId);
        }

        // Create new Message
        ChatMsg chatMsg = ChatMsg.builder()
                .id(new DecimalFormat("00000000").format(new Random().nextInt(99999999)))
                .roomId(chatId)
                .senderId(chatMsgPrivateDTO.getSenderName())
                .content(chatMsgPrivateDTO.getContent())
                .timestamp(new Date(System.currentTimeMillis()))
                .status(MessageStatus.SENT)
                .build();

        // Check if image is present in message content
        if (chatMsgPrivateDTO.getImageContent() != null && !chatMsgPrivateDTO.getImageContent().isEmpty()) {
            String imageId = saveImage(chatMsgPrivateDTO.getImageContent(), chatMsg.getId());
            chatMsg.setImageContentId(imageId);
        }

        // Check if video is present in message content
        if (chatMsgPrivateDTO.getVideoContent() != null && !chatMsgPrivateDTO.getVideoContent().isEmpty()) {
            String videoId = saveVideo(chatMsgPrivateDTO.getVideoContent(), chatMsg.getId());
            chatMsg.setVideoContentId(videoId);
        }

        // Save New Message to DB
        chatMsgRepository.save(chatMsg);

        log.info("New Private Message Saved in DB");

        return 1;
    }

    public List<ChatMsg> findChatMessages(String senderId, String recipientId) {
        String chatId = senderId + "_" + recipientId;


        List<ChatMsg> chatMessages = chatMsgRepository.findByRoomId(chatId);

        if (chatMessages.isEmpty()) {
            // Check if messages exist with opposite roomId
            String secondaryId = recipientId + "_" + senderId;
            List<ChatMsg> secondaryChatMessages = chatMsgRepository.findByRoomId(secondaryId);
            if(!secondaryChatMessages.isEmpty()) {
                log.info("Chat exists. ChatId used is: [{}]", secondaryId);
                return secondaryChatMessages;
            } else {
                log.info("No chat exists between [{}] and [{}]", senderId, recipientId);
                new ArrayList<>();
            }
        }
        log.info("Chat exists. ChatId used is: [{}]", chatId);
        return chatMessages;
    }

    private String saveImage(MultipartFile image, String messageId) throws IOException {
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
        // Create new image
        ImageMongo imageMongo = new ImageMongo();
        imageMongo.setId(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));
        imageMongo.setData(image.getBytes());
        imageMongo.setSize(image.getSize());
        imageMongo.setFormat(image.getContentType());
        imageMongo.setFileName(fileName);
        imageMongo.setType("message_content");
        imageMongo.setAssociatedMessageId(messageId);
        imageMongo.setUploadTimestamp(new Date(System.currentTimeMillis()));

        // save new image in db
        imageMongoRepository.save(imageMongo);

        return imageMongo.getId();
    }

    private String saveVideo(MultipartFile video, String messageId) throws IOException {
        String fileName = StringUtils.cleanPath(video.getOriginalFilename());
        // Create new image
        Video videoMongo = new Video();
        videoMongo.setId(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));
        videoMongo.setData(video.getBytes());
        videoMongo.setSize(video.getSize());
        videoMongo.setFormat(video.getContentType());
        videoMongo.setVideoName(fileName);
        videoMongo.setType("message_content");
        videoMongo.setAssociatedMessageId(messageId);
        videoMongo.setUploadTimestamp(new Date(System.currentTimeMillis()));

        // save new image in db
        videoMongoRepository.save(videoMongo);

        return videoMongo.getId();
    }


}
