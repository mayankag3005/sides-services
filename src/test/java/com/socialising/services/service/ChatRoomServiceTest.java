package com.socialising.services.service;

import com.socialising.services.model.chat.ChatRoom;
import com.socialising.services.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class ChatRoomServiceTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    //    getChatRoomId

    @Test
    public void should_get_chat_room_id_when_exists() {
        // Given
        String senderId = "user1";
        String recipientId = "user2";
        String chatId = senderId + "_" + recipientId;

        ChatRoom chatRoom = ChatRoom.builder().chatId(chatId).senderId(senderId).recipientId(recipientId).build();

        // Mock
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.of(chatRoom));

        // When
        Optional<String> responseChatId = chatRoomService.getChatRoomId(senderId, recipientId, true);

        // Then
        assertTrue(responseChatId.isPresent());
        assertEquals(chatId, responseChatId.get());
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    public void should_get_chat_room_id_when_not_exists_but_create_new_room() {
        // Given
        String senderId = "user1";
        String recipientId = "user2";
        String chatId = senderId + "_" + recipientId;

        // Mock
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.empty());

        // When
        Optional<String> responseChatId = chatRoomService.getChatRoomId(senderId, recipientId, true);

        // Then
        assertTrue(responseChatId.isPresent());
        assertEquals(chatId, responseChatId.get());
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verify(chatRoomRepository, times(2)).save(any(ChatRoom.class));
    }

    @Test
    public void should_not_get_chat_room_id_when_not_exists_and_not_create_new_room() {
        // Given
        String senderId = "user1";
        String recipientId = "user2";

        // Mock
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.empty());

        // When
        Optional<String> responseChatId = chatRoomService.getChatRoomId(senderId, recipientId, false);

        // Then
        assertTrue(responseChatId.isEmpty());
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }
}