package com.socialising.services.service;

import com.socialising.services.config.JwtService;
import com.socialising.services.constants.ChatType;
import com.socialising.services.dto.ChatDTO;
import com.socialising.services.dto.ChatPrivateDTO;
import com.socialising.services.dto.GroupInfoDTO;
import com.socialising.services.model.User;
import com.socialising.services.model.chat.ChatRoom;
import com.socialising.services.model.nosql.Chat;
import com.socialising.services.model.nosql.GroupInfo;
import com.socialising.services.model.nosql.ImageMongo;
import com.socialising.services.repository.UserRepository;
import com.socialising.services.repository.nosql.ChatMsgRepository;
import com.socialising.services.repository.nosql.ChatRepository;
import com.socialising.services.repository.nosql.GroupInfoRepository;
import com.socialising.services.repository.nosql.ImageMongoRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMongoService {

    private final ChatRepository chatRepository;

    private final GroupInfoRepository groupInfoRepository;

    private final UserRepository userRepository;

    private final ImageMongoRepository imageMongoRepository;

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


    public Chat createGroupRoom(ChatDTO chatDTO, String token) {
        String username = getUsernameFromToken(token);

        String chatRoomId = new DecimalFormat("00000000").format(new Random().nextInt(99999999));

        // check if all the participants are valid and exists in DB
        for (String participant: chatDTO.getParticipants()) {
            if (userRepository.findByUsername(participant).isEmpty()) {
                log.info("User [{}] does not exist", participant);
                return null;
            }
        }

        // Create new Chat
        Chat chat = Chat.builder()
                .id(chatRoomId)
                .participants(chatDTO.getParticipants())
                .type(ChatType.GROUP)
                .roomName(chatDTO.getRoomName())
                .infoId("")
                .build();

        // Create Group info and add Admin
        GroupInfo groupInfo = GroupInfo.builder()
                .id(new DecimalFormat("00000000").format(new Random().nextInt(99999999)))
                .adminUser(username)
                .createdTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        // Save Group Info object to DB
        groupInfoRepository.save(groupInfo);

        // Set the group info id to chat
        chat.setInfoId(groupInfo.getId());

        log.info("Chat Group Info Updated");

        // Save in DB
        chatRepository.save(chat);

        log.info("New Chat Saved in DB");

        return chat;
    }

    public GroupInfo updateGroupChatRoomDetails(String roomId, GroupInfoDTO groupInfoDTO, String token) {
        if (!checkIfRoomExists(roomId)) {
            return null;
        }

        // Get the chat
        Chat chat = chatRepository.findById(roomId).get();

        // Check if it is Group chat
        if (chat.getType().equals(ChatType.ONE_ONE)) {
            log.info("Chat is of type one-one");
            return null;
        }

        String username = getUsernameFromToken(token);

        if (!chat.getParticipants().contains(username)) {
            log.info("User [{}] is not part of the Chat room", username);
            return null;
        }

        // Get the Group Info
        GroupInfo groupInfo = groupInfoRepository.findById(chat.getInfoId()).get();

        // Update the room description
        groupInfo.setDescription(groupInfoDTO.getDescription());

        // Save the info to DB
        groupInfoRepository.save(groupInfo);

        log.info("Room description [{}] updated", groupInfo.getDescription());
        return groupInfo;
    }

    public GroupInfo getRoomInfo(String roomId) {
        if (!checkIfRoomExists(roomId)) {
            return null;
        }

        return groupInfoRepository.findById(chatRepository.findById(roomId).get().getInfoId()).get();
    }

    public boolean checkUserInRoom(List<String> users, String username) {
        if (users.contains(username)) {
            log.info("User [{}] exists in ROOM", username);
            return true;
        }
        log.info("User [{}] does not exists in ROOM", username);
        return false;
    }

    public List<String> addUserToRoom(String roomId, String username, String token) {
        if (!checkIfRoomExists(roomId)) {
            return null;
        }

        if (userRepository.findByUsername(username).isEmpty()) {
            log.info("User [{}] does not exists", username);
            return null;
        }

        String tokenUsername = getUsernameFromToken(token);

        // Get the chat
        Chat chat = chatRepository.findById(roomId).get();

        // Check if user adding new user is ADMIN
        if (!groupInfoRepository.findById(chat.getInfoId()).get().getAdminUser().equals(tokenUsername)) {
            log.info("Only ADMIN can add new users to Room");
            return null;
        }

        // Check if user is part of room
        if (checkUserInRoom(chat.getParticipants(), username)) {
            return chat.getParticipants();
        }

        // Add user to participants list
        chat.getParticipants().add(username);

        chatRepository.save(chat);

        log.info("User [{}] added to Room", username);

        return chat.getParticipants();
    }

    public List<String> getUsersOfRoom(String roomId, String token) {
        if (!checkIfRoomExists(roomId)) {
            return null;
        }

        String tokenUsername = getUsernameFromToken(token);

        // Get the chat
        Chat chat = chatRepository.findById(roomId).get();

        // Check if user is part of room
        if (checkUserInRoom(chat.getParticipants(), tokenUsername)) {
            return chat.getParticipants();
        }

        return null;
    }

    public int removeUserFromRoom(String roomId, String username, String token) {
        if (!checkIfRoomExists(roomId)) {
            return -1;
        }

        if (userRepository.findByUsername(username).isEmpty()) {
            log.info("User [{}] does not exists in DB", username);
            return -1;
        }

        String tokenUsername = getUsernameFromToken(token);

        // Get the chat
        Chat chat = chatRepository.findById(roomId).get();

        // Check if user adding new user is ADMIN
        if (!groupInfoRepository.findById(chat.getInfoId()).get().getAdminUser().equals(tokenUsername)) {
            log.info("Only ADMIN can remove users from Room");
            return -1;
        }

        // Check if user is part of room
        if (!checkUserInRoom(chat.getParticipants(), username)) {
            return 0;
        }

        // Remove user to participants list
        chat.getParticipants().remove(username);

        chatRepository.save(chat);

        log.info("User [{}] removed from Room", username);

        return 1;
    }

    public int leaveRoom(String roomId, String token) {
        if (!checkIfRoomExists(roomId)) {
            return -1;
        }

        // Get the chat
        Chat chat = chatRepository.findById(roomId).get();

        // Get the username from token
        String username = getUsernameFromToken(token);

        // Check if user is part of room
        if (!checkUserInRoom(chat.getParticipants(), username)) {
            log.info("User [{}] does not exist in ROOM", username);
            return 0;
        }

        // Remove user to participants list
        chat.getParticipants().remove(username);

        chatRepository.save(chat);

        log.info("User [{}] left the Room", username);

        return 1;
    }

    public ImageMongo addGroupProfilePicture(MultipartFile image, String roomId) throws IOException {
        if (image == null || image.isEmpty()) {
            log.info("Please provide an image");
            return null;
        }

        if (!checkIfRoomExists(roomId)) {
            return null;
        }

        GroupInfo groupInfo = groupInfoRepository.findById(chatRepository.findById(roomId).get().getInfoId()).get();

        var existingDP = imageMongoRepository.findByAssociatedRoomId(roomId);

        String fileName = StringUtils.cleanPath(image.getOriginalFilename());

        // Create new image
        ImageMongo imageMongo = new ImageMongo();
        imageMongo.setId(new DecimalFormat("00000000").format(new Random().nextInt(99999999)));
        imageMongo.setData(image.getBytes());
        imageMongo.setSize(image.getSize());
        imageMongo.setFormat(image.getContentType());
        imageMongo.setFileName(fileName);
        imageMongo.setType("group_profile_picture");
        imageMongo.setAssociatedRoomId(roomId);
        imageMongo.setUploadTimestamp(new Date(System.currentTimeMillis()));

        // save new image in db
        imageMongoRepository.save(imageMongo);

        if (existingDP.isPresent()) {
            log.info("Group DP exists already. Removing it");
            ImageMongo existingImage = existingDP.get();
            imageMongoRepository.delete(existingImage);
        }

        // Save image id in user dp id
        groupInfo.setRoomPicture(imageMongo.getId());

        groupInfoRepository.save(groupInfo);
        log.info("Chat Room Profile Picture added successfully");

        return imageMongo;
    }

    public ImageMongo getGroupProfilePicture(String roomId) {
        if (!checkIfRoomExists(roomId)) {
            return null;
        }

        var existingDP = imageMongoRepository.findByAssociatedRoomId(roomId);

        if(existingDP.isPresent()) {
            log.info("Group Profile Picture exists");
            return existingDP.get();
        }
        log.info("Group Profile picture does not exists");
        return null;
    }

    public int removeGroupProfilePicture(String roomId) {
        if (!checkIfRoomExists(roomId)) {
            return -1;
        }

        var existingDP = imageMongoRepository.findByAssociatedRoomId(roomId);

        // Check if DP exists
        if (existingDP.isEmpty()) {
            log.info("Group profile picture does not exists in DB");
            return -1;
        }

        log.info("Group DP exists already. Deleting it");

        // Removing DP from DB
        imageMongoRepository.delete(existingDP.get());

        // Save null in chat room profile picture id
        GroupInfo groupInfo = groupInfoRepository.findById(chatRepository.findById(roomId).get().getInfoId()).get();
        groupInfo.setRoomPicture("");

        groupInfoRepository.save(groupInfo);

        log.info("User DP removed successfully");

        return 1;
    }

    // private chat (1-1)

    public Chat createPrivateChat(ChatPrivateDTO chatPrivateDTO) {
        // check if all both senderName and recipient Name are valid and exists in DB
        if (userRepository.findByUsername(chatPrivateDTO.getSenderName()).isEmpty() || userRepository.findByUsername(chatPrivateDTO.getRecipientName()).isEmpty()) {
            log.info("One of the User does not exist in DB");
            return null;
        }

        String privateChatIdAndName = chatPrivateDTO.getSenderName() + "_" + chatPrivateDTO.getRecipientName();

        // Create new Chat
        Chat chat = Chat.builder()
                .id(privateChatIdAndName)
                .participants(List.of(chatPrivateDTO.getSenderName(), chatPrivateDTO.getRecipientName()))
                .type(ChatType.ONE_ONE)
                .roomName(privateChatIdAndName)
                .infoId("")
                .build();

        // Save in DB
        chatRepository.save(chat);

        log.info("New Private Chat Saved in DB");

        return chat;
    }

    public String getPrivateChatRoomId(ChatPrivateDTO chatPrivateDTO, boolean createNewRoomIfNotExists)  {

        String privateChatId = chatPrivateDTO.getSenderName() + "_" + chatPrivateDTO.getRecipientName();

        var chatRoomOpt = chatRepository.findById(privateChatId);

        if (chatRoomOpt.isEmpty()) {

            // check with opposite id
            String secondaryChatId = chatPrivateDTO.getRecipientName() + "_" + chatPrivateDTO.getSenderName();
            var secondaryChatRoomOpt = chatRepository.findById(secondaryChatId);
            if(secondaryChatRoomOpt.isEmpty()) {
                if (createNewRoomIfNotExists) {
                    log.info("Creating new chat room since it does not exists");
                    return createPrivateChat(chatPrivateDTO).getId();
                } else {
                    log.info("No chat room exists");
                    return "";
                }
            } else {
                log.info("Chat room exists with id: [{}]", secondaryChatId);
                secondaryChatRoomOpt.get().getId();
            }
        }

        return chatRoomOpt.get().getId();
    }


}
