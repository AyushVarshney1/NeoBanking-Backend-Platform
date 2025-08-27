package com.neobank.neobankauditservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.neobank.neobankauditservice.model.AuditLog;
import com.neobank.neobankauditservice.model.enums.TransactionStatus;
import com.neobank.neobankauditservice.model.enums.TransactionType;
import com.neobank.neobankauditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import transaction.events.BalanceUpdateEvent;
import transaction.events.TransferEvent;

@Service
@RequiredArgsConstructor
public class AuditEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditEventConsumer.class);
    private final AuditService auditService;

    @KafkaListener(topics = "transaction.balance.updated.v1", groupId = "neobank-audit-service")
    public void consumeBalanceUpdatedEvent(byte[] event){
        try{
            BalanceUpdateEvent balanceUpdateEvent = BalanceUpdateEvent.parseFrom(event);
            log.info("Received Balance Updated Event : {}",  balanceUpdateEvent);

            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(balanceUpdateEvent.getUserId());
            auditLog.setTransactionId(balanceUpdateEvent.getTransactionId());
            auditLog.setAccountNumber(balanceUpdateEvent.getAccountNumber());
            auditLog.setAmount(balanceUpdateEvent.getAmount());
            auditLog.setStatus(balanceUpdateEvent.getStatus().equals("SUCCESS") ? TransactionStatus.SUCCESS :  TransactionStatus.FAILED);
            auditLog.setType(balanceUpdateEvent.getTypeOfTransaction().equals("DEPOSIT") ? TransactionType.DEPOSIT : TransactionType.WITHDRAW);

            auditService.saveAuditLog(auditLog);

        }  catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "transaction.amount.transferred.v1", groupId = "neobank-audit-service")
    public void consumeTransferredEvent(byte[] event){
        try{
            TransferEvent transferEvent = TransferEvent.parseFrom(event);
            log.info("Received Transfer Event : {}",  transferEvent);

            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(transferEvent.getUserId());
            auditLog.setTransactionId(transferEvent.getTransactionId());
            auditLog.setAccountNumber(transferEvent.getAccountNumber());
            auditLog.setAmount(transferEvent.getAmount());
            auditLog.setStatus(transferEvent.getStatus().equals("SUCCESS") ? TransactionStatus.SUCCESS : TransactionStatus.FAILED);
            auditLog.setType(TransactionType.TRANSFER);
            auditLog.setBeneficiaryAccountNumber(transferEvent.getBeneficiaryAccountNumber());

            auditService.saveAuditLog(auditLog);

        }  catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }

}
