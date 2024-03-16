package com.example.chatroomskafkabackend2.converter;

import com.example.chatroomskafkabackend2.pojo.Message;
import org.springframework.kafka.support.serializer.JsonSerde;

public class ChatMessageSerDes extends JsonSerde<Message> {
    public ChatMessageSerDes() {
        super();
        ignoreTypeHeaders();
    }
}
