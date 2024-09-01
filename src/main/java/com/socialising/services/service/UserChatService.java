package com.socialising.services.service;

import com.socialising.services.model.nosql.UserChat;
import com.socialising.services.repository.nosql.UserChatRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserChatService {

    private final UserChatRepository userChatRepository;

    // Method to add a chat room ID to a user's list of chats
    public void addChatToUser(String username, String roomId) {
        Optional<UserChat> userChatOptional = userChatRepository.findByUsername(username);

        UserChat userChat;
        if (userChatOptional.isPresent()) {
            userChat = userChatOptional.get();
        } else {
            userChat = new UserChat();
            userChat.setUsername(username);
        }

        if(userChat.getChats() == null) {
            userChat.setChats(new ArrayList<>());
        }

        if (!userChat.getChats().contains(roomId)) {
            userChat.getChats().add(roomId);
            userChatRepository.save(userChat);
            log.info("User [{}] added to the following Chat [{}]", username, roomId);
        }
    }

    // Method to get all the chats of user
    public List<String> getChatsOfUser(String username) {
        Optional<UserChat> userChatOptional = userChatRepository.findByUsername(username);

        UserChat userChat;
        if (userChatOptional.isPresent()) {
            userChat = userChatOptional.get();
        } else {
            log.info("No chats for the user [{}]", username);
            return null;
        }

        if(userChat.getChats() == null) {
            log.info("No chats for the user [{}]", username);
            return new ArrayList<>();
        }

        log.info("Chats for user [{}] are {}", username, userChat.getChats());
        return userChat.getChats();
    }

    // Method to remove a chat room ID from a user's list of chats
    public void removeChatFromUser(String username, String roomId) {
        Optional<UserChat> userChatOptional = userChatRepository.findByUsername(username);

        if (userChatOptional.isPresent()) {
            UserChat userChat = userChatOptional.get();
            userChat.getChats().remove(roomId);
            userChatRepository.save(userChat);
            log.info("User [{}] removed/left from the following Chat [{}]", username, roomId);
        }
    }
}

