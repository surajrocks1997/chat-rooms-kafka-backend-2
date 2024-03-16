package com.example.chatroomskafkabackend2.service;

import com.example.chatroomskafkabackend2.pojo.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final StreamBridge streamBridge;

    public void produce(Message message) {
        org.springframework.messaging.Message<Message> build = MessageBuilder.withPayload(message).build();
        streamBridge.send("producer-out-0", build);

    }
}
