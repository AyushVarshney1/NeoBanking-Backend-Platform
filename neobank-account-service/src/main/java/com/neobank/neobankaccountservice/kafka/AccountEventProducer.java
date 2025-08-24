package com.neobank.neobankaccountservice.kafka;

import account.events.AccountCreatedEvent;
import account.events.AccountStatusUpdatedEvent;
import account.events.KycCompletedEvent;
import com.neobank.neobankaccountservice.dto.AccountProducerDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountEventProducer {

    private static final Logger log = LoggerFactory.getLogger(AccountEventProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendAccountCreatedEvent(AccountProducerDto accountProducerDto) {
        AccountCreatedEvent accountCreatedEvent = AccountCreatedEvent.newBuilder()
                .setEmail(accountProducerDto.getEmail())
                .setAccountNumber(accountProducerDto.getAccountNumber())
                .build();

        byte[] eventBytes = accountCreatedEvent.toByteArray();

        try{
            kafkaTemplate.send("account.created.v1", eventBytes);
            log.info("Sent Account Created Event : {}", accountCreatedEvent);
        }catch(Exception e){
            log.error("Error sending Account Created Event : {}", accountCreatedEvent);
        }
    }

    public void sendAccountStatusUpdatedEvent(AccountProducerDto accountProducerDto) {
        AccountStatusUpdatedEvent accountStatusUpdatedEvent = AccountStatusUpdatedEvent.newBuilder()
                .setEmail(accountProducerDto.getEmail())
                .setAccountNumber(accountProducerDto.getAccountNumber())
                .setStatus(accountProducerDto.getStatus())
                .setBalance(accountProducerDto.getBalance())
                .build();

        byte[] eventBytes = accountStatusUpdatedEvent.toByteArray();

        try{
            kafkaTemplate.send("account.status.updated.v1", eventBytes);
            log.info("Sent Account Status Updated Event : {}", accountStatusUpdatedEvent);
        }catch(Exception e){
            log.error("Error sending Account Status Updated Event : {}", accountStatusUpdatedEvent);
        }
    }

    public void sendKycCompletedEvent(AccountProducerDto accountProducerDto) {
        KycCompletedEvent kycCompletedEvent = KycCompletedEvent.newBuilder()
                .setEmail(accountProducerDto.getEmail())
                .setAccountNumber(accountProducerDto.getAccountNumber())
                .build();

        byte[] eventBytes = kycCompletedEvent.toByteArray();

        try{
            kafkaTemplate.send("account.kyc.completed.v1", eventBytes);
            log.info("Sent Account KYC Completed Event : {}", kycCompletedEvent);
        }catch(Exception e){
            log.error("Error sending Account KYC Completed Event : {}", kycCompletedEvent);
        }
    }


}
