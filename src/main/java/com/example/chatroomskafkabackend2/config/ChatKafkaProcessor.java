package com.example.chatroomskafkabackend2.config;

import com.example.chatroomskafkabackend2.pojo.Message;
import com.example.chatroomskafkabackend2.service.WebSocketSubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ChatKafkaProcessor {

    private final WebSocketSubscriberService webSocketSubscriberService;

//    @Bean
//    public Supplier<org.springframework.messaging.Message<Message>> producer() {
//        return () -> {
//            ChatRoomName randomChatRoomName = ChatRoomName.values()[new Random().nextInt(ChatRoomName.values().length)];
//            Message message = new Message("Suraj", "Testing", String.valueOf(System.currentTimeMillis()), randomChatRoomName.name());
//            return MessageBuilder
//                    .withPayload(message)
//                    .build();
//        };
//    }

    @Bean
    public Consumer<KStream<String, Message>> consumer() {
        return kStream -> kStream
                .peek((key, value) -> log.info("Consumer Data: {} ", value))
                .foreach((key, value) -> webSocketSubscriberService.sendToSubscriber(value, value.getChatRoomName()));
    }

    @Bean
    public Consumer<KStream<String, Message>> aggregate() {
        return kStream -> kStream
                .groupBy((key, value) -> value.getChatRoomName(), Grouped.with(Serdes.String(), new JsonSerde<>(Message.class)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10)))
                .aggregate(() -> 0L,
                        (key, value, aggregate) -> aggregate + 1,
                        Materialized.with(Serdes.String(), Serdes.Long()))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .map((key, value) -> new KeyValue<>(key.key(), value.toString()))
                .peek((key, value) -> log.info("Aggregated Data: Key: {}   -   Value: {}", key, value));
    }
}
