//package com.socialising.services.config;
//
//import com.socialising.services.constants.MessageType;
//import com.socialising.services.controller.PostController;
//import com.socialising.services.model.chat.ChatMessage;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@Component
//@RequiredArgsConstructor
//public class WebSocketEventListener {
//
//    private static final Logger log = LoggerFactory.getLogger(PostController.class);
//
//    private final SimpMessageSendingOperations messageTemplate;
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event)
//    {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//
//        if(username != null) {
//            log.info("User [{}] Disconnected", username);
//            var chatMessage = ChatMessage.builder()
//                    .type(MessageType.LEAVE)
//                    .sender(username)
//                    .build();
//
//            // Inform all the users that a user has left the session
//            messageTemplate.convertAndSend("/user/public", chatMessage);
//        }
//    }
//}
