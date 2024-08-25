package com.socialising.services.service;

import com.socialising.services.model.chat.ChatMessage;
import com.socialising.services.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatMessageService chatMessageService;

    // saveMessage

    @Test
    public void should_save_message() {
        // Given
        String senderId = "user1";
        String recipientId = "user2";
        String chatId = senderId + "_" + recipientId;

        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .content("Test chat message")
                .build();

        // Mock
        when(chatRoomService.getChatRoomId(senderId, recipientId, true)).thenReturn(Optional.of(chatId));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // When
        ChatMessage responseMessage = chatMessageService.saveMessage(chatMessage);

        // Then
        assertNotNull(responseMessage);
        assertEquals(chatId, responseMessage.getChatId());
        assertEquals("Test chat message", responseMessage.getContent());
        verify(chatRoomService, times(1)).getChatRoomId(senderId, recipientId, true);
        verify(chatMessageRepository, times(1)).save(chatMessage);
    }

    @Test
    public void should_not_save_message_when_chat_room_not_found() {
        // Given
        String senderId = "user1";
        String recipientId = "user2";

        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .content("Test chat message")
                .build();

        // Mock
        when(chatRoomService.getChatRoomId(senderId, recipientId, true)).thenReturn(Optional.empty());

        // When
        assertThrows(RuntimeException.class, () -> chatMessageService.saveMessage(chatMessage));

        // Then
        verify(chatRoomService, times(1)).getChatRoomId(senderId, recipientId, true);
        verify(chatMessageRepository, never()).save(chatMessage);
    }

    // findChatMessages

    @Test
    public void should_get_messages_when_room_exists() {
        // Given
        String senderId = "user1";
        String recipientId = "user2";
        String chatId = senderId + "_" + recipientId;

        ChatMessage chatFromSender = ChatMessage.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .content("Hello from Sender")
                .build();

        ChatMessage chatFromRecipient = ChatMessage.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .content("Hi from Recipient")
                .build();

        List<ChatMessage> messages = List.of(chatFromSender, chatFromRecipient);

        // Mock
        when(chatRoomService.getChatRoomId(senderId, recipientId, false)).thenReturn(Optional.of(chatId));
        when(chatMessageRepository.findByChatId(chatId)).thenReturn(messages);

        // When
        List<ChatMessage> responseMessages = chatMessageService.findChatMessages(senderId, recipientId);

        // Then
        assertEquals(messages, responseMessages);
        assertEquals(2, responseMessages.size());
        verify(chatRoomService, times(1)).getChatRoomId(senderId, recipientId, false);
        verify(chatMessageRepository, times(1)).findByChatId(chatId);
    }

    @Test
    public void should_not_get_messages_when_room_not_exists() {
        // Given
        String senderId = "user1";
        String recipientId = "user2";

        // Mock
        when(chatRoomService.getChatRoomId(senderId, recipientId, false)).thenReturn(Optional.empty());

        // When
        List<ChatMessage> responseMessages = chatMessageService.findChatMessages(senderId, recipientId);

        // Then
        assertEquals(new ArrayList<>(), responseMessages);
        assertEquals(0, responseMessages.size());
        verify(chatRoomService, times(1)).getChatRoomId(senderId, recipientId, false);
        verify(chatMessageRepository, never()).findByChatId(anyString());
    }
}