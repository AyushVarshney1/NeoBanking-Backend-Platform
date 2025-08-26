package com.neobank.neobanktransactionservice.kafka;

import com.neobank.neobanktransactionservice.dto.TransactionProducerDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import transaction.events.BalanceUpdateEvent;
import transaction.events.TransactionEvent;
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
                .setTypeOfTransaction(transactionProducerDto.getTypeOfTransaction())
                .setAmount(transactionProducerDto.getAmount())
                .setBalance(transactionProducerDto.getBalance())
                .setEmail(transactionProducerDto.getEmail())
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
                .setBalance(transactionProducerDto.getBalance())
                .setEmail(transactionProducerDto.getEmail())
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
