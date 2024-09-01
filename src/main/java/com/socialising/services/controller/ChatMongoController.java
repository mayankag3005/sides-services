package com.socialising.services.controller;

import com.socialising.services.dto.ChatDTO;
import com.socialising.services.dto.ChatPrivateDTO;
import com.socialising.services.dto.GroupInfoDTO;
import com.socialising.services.model.nosql.Chat;
import com.socialising.services.model.nosql.GroupInfo;
import com.socialising.services.model.nosql.ImageMongo;
import com.socialising.services.service.ChatMongoService;
import com.twilio.http.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Chat> createRoom(@RequestBody ChatDTO chatDTO,  @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatMongoService.createGroupRoom(chatDTO, token));
    }

    @PutMapping("/updateRoomInfo/{roomId}")
    public ResponseEntity<GroupInfo> updateRoomInfo(@PathVariable String roomId, @RequestBody GroupInfoDTO groupInfoDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatMongoService.updateGroupChatRoomDetails(roomId, groupInfoDTO, token));
    }

    @GetMapping("/roomInfo/{roomId}")
    public ResponseEntity<GroupInfo> getRoomInfo(@PathVariable String roomId) {
        return ResponseEntity.ok(chatMongoService.getRoomInfo(roomId));
    }

    @PostMapping("/addNewUser/{roomId}/{username}")
    public ResponseEntity<List<String>> addNewUserToRoom(@PathVariable("roomId") String roomId, @PathVariable("username") String username, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatMongoService.addUserToRoom(roomId, username, token));
    }

    @GetMapping("/getUsers/{roomId}")
    public ResponseEntity<List<String>> addNewUserToRoom(@PathVariable("roomId") String roomId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatMongoService.getUsersOfRoom(roomId, token));
    }

    @DeleteMapping("/removeUser/{roomId}/{username}")
    public ResponseEntity<Integer> removeUserFromRoom(@PathVariable("roomId") String roomId, @PathVariable("username") String username, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatMongoService.removeUserFromRoom(roomId, username, token));
    }

    @DeleteMapping("/leaveRoom/{roomId}")
    public ResponseEntity<Integer> leaveRoom(@PathVariable("roomId") String roomId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatMongoService.leaveRoom(roomId, token));
    }

    @PostMapping("/room/addDP/{roomId}")
    public ResponseEntity<ImageMongo> addRoomProfilePicture(@PathVariable String roomId, @RequestBody MultipartFile file) throws Exception {
        return ResponseEntity.ok(chatMongoService.addGroupProfilePicture(file, roomId));
    }

    @GetMapping("/room/getDP/{roomId}")
    public ResponseEntity<ImageMongo> getRoomProfilePicture(@PathVariable String roomId) throws Exception {
        return ResponseEntity.ok(chatMongoService.getGroupProfilePicture(roomId));
    }

    @DeleteMapping("/room/removeDP/{roomId}")
    public ResponseEntity<Integer> removeRoomProfilePicture(@PathVariable String roomId) {
        return ResponseEntity.ok(chatMongoService.removeGroupProfilePicture(roomId));
    }

    // 1-1 Chat

    @PostMapping("/createPrivateChat")
    public ResponseEntity<Chat> createPrivateChatRoom(@RequestBody ChatPrivateDTO chatPrivateDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatMongoService.createPrivateChat(chatPrivateDTO, token));
    }

    @GetMapping("/getPrivateChatId")
    public ResponseEntity<String> getPrivateChatRoomId(@RequestBody ChatPrivateDTO chatPrivateDTO) {
        return ResponseEntity.ok(chatMongoService.getPrivateChatRoomId(chatPrivateDTO, false));
    }

    // User level

    @GetMapping("/getChatsOfUser")
    public ResponseEntity<List<String>> getChatsOfUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatMongoService.getChatsOfUser(token));
    }
}
