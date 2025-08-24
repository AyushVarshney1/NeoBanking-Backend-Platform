package com.neobank.authservice.kafka;

import auth.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthEventProducer {

    private static final Logger log = LoggerFactory.getLogger(AuthEventProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendUserCreatedEvent(String email){
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.newBuilder()
                .setEmail(email)
                .build();

        byte[] eventBytes = userCreatedEvent.toByteArray();

        try{
            kafkaTemplate.send("auth.user.created.v1", eventBytes);
            log.info("Sent User Created Event : {}", userCreatedEvent);
        }catch(Exception e){
            log.error("Error sending User Created Event : {}", userCreatedEvent);
        }

    }
}
