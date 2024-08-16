package com.socialising.services.controller;

import com.socialising.services.dto.ChatDTO;
import com.socialising.services.dto.ChatPrivateDTO;
import com.socialising.services.dto.GroupInfoDTO;
import com.socialising.services.model.nosql.Chat;
import com.socialising.services.model.nosql.GroupInfo;
import com.socialising.services.model.nosql.ImageMongo;
import com.socialising.services.service.ChatMongoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatMongoController {

    private final ChatMongoService chatMongoService;

    @PostMapping("/createRoom")
    public Chat createRoom(@RequestBody ChatDTO chatDTO,  @RequestHeader("Authorization") String token) {
        return chatMongoService.createGroupRoom(chatDTO, token);
    }

    @PutMapping("/updateRoomInfo/{roomId}")
    public GroupInfo updateRoomInfo(@PathVariable String roomId, @RequestBody GroupInfoDTO groupInfoDTO, @RequestHeader("Authorization") String token) {
        return chatMongoService.updateGroupChatRoomDetails(roomId, groupInfoDTO, token);
    }

    @GetMapping("/roomInfo/{roomId}")
    public GroupInfo getRoomInfo(@PathVariable String roomId) {
        return chatMongoService.getRoomInfo(roomId);
    }

    @PostMapping("/addNewUser/{roomId}/{username}")
    public List<String> addNewUserToRoom(@PathVariable("roomId") String roomId, @PathVariable("username") String username, @RequestHeader("Authorization") String token) {
        return chatMongoService.addUserToRoom(roomId, username, token);
    }

    @GetMapping("/getUsers/{roomId}")
    public List<String> addNewUserToRoom(@PathVariable("roomId") String roomId, @RequestHeader("Authorization") String token) {
        return chatMongoService.getUsersOfRoom(roomId, token);
    }

    @DeleteMapping("/removeUser/{roomId}/{username}")
    public int removeUserFromRoom(@PathVariable("roomId") String roomId, @PathVariable("username") String username, @RequestHeader("Authorization") String token) {
        return chatMongoService.removeUserFromRoom(roomId, username, token);
    }

    @DeleteMapping("/leaveRoom/{roomId}")
    public int leaveRoom(@PathVariable("roomId") String roomId, @RequestHeader("Authorization") String token) {
        return chatMongoService.leaveRoom(roomId, token);
    }

    @PostMapping("/room/addDP/{roomId}")
    public ImageMongo addRoomProfilePicture(@PathVariable String roomId, @RequestBody MultipartFile file) throws Exception {
        return chatMongoService.addGroupProfilePicture(file, roomId);
    }

    @GetMapping("/room/getDP/{roomId}")
    public ImageMongo getRoomProfilePicture(@PathVariable String roomId) throws Exception {
        return chatMongoService.getGroupProfilePicture(roomId);
    }

    @DeleteMapping("/room/removeDP/{roomId}")
    public int removeRoomProfilePicture(@PathVariable String roomId) {
        return chatMongoService.removeGroupProfilePicture(roomId);
    }

    // 1-1 Chat

    @PostMapping("/createPrivateChat")
    public Chat createPrivateChatRoom(@RequestBody ChatPrivateDTO chatPrivateDTO) {
        return chatMongoService.createPrivateChat(chatPrivateDTO);
    }

    @GetMapping("/getPrivateChatId")
    public String getPrivateChatRoomId(@RequestBody ChatPrivateDTO chatPrivateDTO) {
        return chatMongoService.getPrivateChatRoomId(chatPrivateDTO, true);
    }
}
