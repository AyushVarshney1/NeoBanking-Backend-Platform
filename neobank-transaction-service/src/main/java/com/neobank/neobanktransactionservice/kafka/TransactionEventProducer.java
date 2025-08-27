package com.neobank.neobanktransactionservice.kafka;

import com.neobank.neobanktransactionservice.dto.TransactionProducerDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import transaction.events.BalanceUpdateEvent;
import transaction.events.TransferEvent;

@Service
@RequiredArgsConstructor
public class TransactionEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendBalanceUpdateEvent(TransactionProducerDto transactionProducerDto) {
        BalanceUpdateEvent balanceUpdateEvent = BalanceUpdateEvent.newBuilder()
                .setUserId(transactionProducerDto.getUserId())
                .setTransactionId(transactionProducerDto.getTransactionId())
                .setAccountNumber(transactionProducerDto.getAccountNumber())
                .setTypeOfTransaction(transactionProducerDto.getTypeOfTransaction().toString())
                .setStatus(transactionProducerDto.getTransactionStatus().toString())
                .setAmount(transactionProducerDto.getAmount())
                // THE -1.0 below is just a placeholder, as null cant be set to our protobuf field, and this -1.0 wont be consumed by notification service as it will be a failed transaction because if balance field is empty in dto, then that would mean the status would be FAILED, so notification service will skip this event, so it wont send out wrong balance value of -1.0. Also Audit service doesnt need balance value, it would just ignore the -1.0
                .setBalance(transactionProducerDto.getBalance() == null ? -1.0 : transactionProducerDto.getBalance())
                // THE "" below is just a placeholder, as null cant be set to our protobuf field, and this "" wont be consumed by notification service as it will be a failed transaction because if email field is empty in dto, then that would mean the status would be FAILED, so notification service will skip this event, so it wont send out wrong email value of "". Also Audit Service doesnt need the email value, so it will just ignore the email value ""
                .setEmail(transactionProducerDto.getEmail() == null ? "" : transactionProducerDto.getEmail())
                .build();

        byte[] eventBytes = balanceUpdateEvent.toByteArray();

        try{
            kafkaTemplate.send("transaction.balance.updated.v1", eventBytes);
            log.info("Sent Balance Updated Event : {}", balanceUpdateEvent);
        }catch (Exception e){
            log.error("Error sending Balance Updated Event : {}", balanceUpdateEvent);
        }
    }

    public void sendAmountTransferEvent(TransactionProducerDto transactionProducerDto) {
        TransferEvent amountTransferEvent = TransferEvent.newBuilder()
                .setUserId(transactionProducerDto.getUserId())
                .setTransactionId(transactionProducerDto.getTransactionId())
                .setAccountNumber(transactionProducerDto.getAccountNumber())
                .setBeneficiaryAccountNumber(transactionProducerDto.getBeneficiaryNumber())
                .setAmount(transactionProducerDto.getAmount())
                .setStatus(transactionProducerDto.getTransactionStatus().toString())
                // THE -1.0 below is just a placeholder, as null cant be set to our protobuf field, and this -1.0 wont be consumed by notification service as it will be a failed transaction because if balance field is empty in dto, then that would mean the status would be FAILED, so notification service will skip this event, so it wont send out wrong balance value of -1.0. Also Audit service doesnt need balance value, it would just ignore the -1.0
                .setBalance(transactionProducerDto.getBalance() == null ? -1.0 : transactionProducerDto.getBalance())
                // THE "" below is just a placeholder, as null cant be set to our protobuf field, and this "" wont be consumed by notification service as it will be a failed transaction because if email field is empty in dto, then that would mean the status would be FAILED, so notification service will skip this event, so it wont send out wrong email value of "". Also Audit Service doesnt need the email value, so it will just ignore the email value ""
                .setEmail(transactionProducerDto.getEmail() == null ? "" : transactionProducerDto.getEmail())
                .build();

        byte[] eventBytes = amountTransferEvent.toByteArray();

        try{
            kafkaTemplate.send("transaction.amount.transferred.v1", eventBytes);
            log.info("Sent Amount Transfer Event : {}", amountTransferEvent);
        }catch (Exception e){
            log.error("Error sending Amount Transfer Event : {}", amountTransferEvent);
        }
    }

}
