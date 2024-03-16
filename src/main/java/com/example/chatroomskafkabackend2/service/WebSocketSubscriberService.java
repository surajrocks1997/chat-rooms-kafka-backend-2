package com.example.chatroomskafkabackend2.service;

import com.example.chatroomskafkabackend2.pojo.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketSubscriberService {

    private final SimpMessageSendingOperations messageTemplate;

    public void sendToSubscriber(Message message, String chatRoomName) {
        this.messageTemplate.convertAndSend("/topic/chatRoom." + chatRoomName, message);
    }
}
