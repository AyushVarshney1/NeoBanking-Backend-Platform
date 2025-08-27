package com.neobank.neobanknotificationservice.kafka;

import account.events.AccountCreatedEvent;
import account.events.AccountStatusUpdatedEvent;
import account.events.KycCompletedEvent;
import auth.events.UserCreatedEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import com.neobank.neobanknotificationservice.model.Notification;
import com.neobank.neobanknotificationservice.model.enums.NotificationType;
import com.neobank.neobanknotificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import transaction.events.BalanceUpdateEvent;
import transaction.events.TransferEvent;

@Service
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);
    private final NotificationService notificationService;

    @KafkaListener(topics = "auth.user.created.v1", groupId = "neobank-notification-service")
    public void consumeAuthUserCreatedEvent(byte[] event){
        try{
            UserCreatedEvent userCreatedEvent = UserCreatedEvent.parseFrom(event);
            log.info("Received User Created Event : {}",  userCreatedEvent);

            Notification notification = new Notification();
            notification.setUserId(userCreatedEvent.getUserId());
            notification.setEmail(userCreatedEvent.getEmail());
            notification.setType(NotificationType.USER_CREATED);

            notificationService.sendUserCreatedNotification(notification);

        }  catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "account.created.v1", groupId = "neobank-notification-service")
    public void consumeAccountCreatedEvent(byte[] event){
        try{
            AccountCreatedEvent accountCreatedEvent = AccountCreatedEvent.parseFrom(event);
            log.info("Received Account Created Event : {}",  accountCreatedEvent);

            Notification notification = new Notification();
            notification.setUserId(accountCreatedEvent.getUserId());
            notification.setEmail(accountCreatedEvent.getEmail());
            notification.setType(NotificationType.ACCOUNT_CREATED);
            notification.setAccountNumber(accountCreatedEvent.getAccountNumber());

            notificationService.sendAccountCreatedNotification(notification);

        }  catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "account.status.updated.v1", groupId = "neobank-notification-service")
    public void consumeAccountStatusUpdatedEvent(byte[] event){
        try{
            AccountStatusUpdatedEvent accountStatusUpdatedEvent = AccountStatusUpdatedEvent.parseFrom(event);
            log.info("Received Account Status Updated Event : {}",  accountStatusUpdatedEvent);

            Notification notification = new Notification();
            notification.setUserId(accountStatusUpdatedEvent.getUserId());
            notification.setEmail(accountStatusUpdatedEvent.getEmail());
            notification.setType(accountStatusUpdatedEvent.getStatus().equals("Activated") ?  NotificationType.ACCOUNT_ACTIVATED : NotificationType.ACCOUNT_DEACTIVATED);
            notification.setAccountNumber(accountStatusUpdatedEvent.getAccountNumber());

            notificationService.sendAccountStatusUpdatedNotification(notification);

        }  catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "account.kyc.completed.v1", groupId = "neobank-notification-service")
    public void consumeKycCompletedEvent(byte[] event){
        try{
            KycCompletedEvent kycCompletedEvent = KycCompletedEvent.parseFrom(event);
            log.info("Received KYC Completed Event : {}",  kycCompletedEvent);

            Notification notification = new Notification();
            notification.setUserId(kycCompletedEvent.getUserId());
            notification.setEmail(kycCompletedEvent.getEmail());
            notification.setType(NotificationType.ACCOUNT_KYC_COMPLETED);
            notification.setAccountNumber(kycCompletedEvent.getAccountNumber());

            notificationService.sendKycCompletedNotification(notification);

        }  catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "transaction.balance.updated.v1", groupId = "neobank-notification-service")
    public void consumeBalanceUpdatedEvent(byte[] event){
        try{
            BalanceUpdateEvent balanceUpdateEvent = BalanceUpdateEvent.parseFrom(event);
            log.info("Received Balance Updated Event : {}",  balanceUpdateEvent);

            Notification notification = new Notification();
            notification.setUserId(balanceUpdateEvent.getUserId());
            notification.setEmail(balanceUpdateEvent.getEmail());
            notification.setType(balanceUpdateEvent.getTypeOfTransaction().equals("DEPOSIT") ? NotificationType.TRANSACTION_DEPOSITED : NotificationType.TRANSACTION_WITHDRAWN);
            notification.setAccountNumber(balanceUpdateEvent.getAccountNumber());

            if(balanceUpdateEvent.getStatus().equals("SUCCESS")){
                notificationService.sendBalanceUpdatedNotification(notification, balanceUpdateEvent.getBalance(), balanceUpdateEvent.getAmount(), balanceUpdateEvent.getTransactionId());
            }

        }  catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "transaction.amount.transferred.v1", groupId = "neobank-notification-service")
    public void consumeTransferredEvent(byte[] event){
        try{
            TransferEvent transferEvent = TransferEvent.parseFrom(event);
            log.info("Received Transfer Event : {}",  transferEvent);

            Notification notification = new Notification();
            notification.setUserId(transferEvent.getUserId());
            notification.setEmail(transferEvent.getEmail());
            notification.setType(NotificationType.TRANSACTION_TRANSFERRED);
            notification.setAccountNumber(transferEvent.getAccountNumber());

            if(transferEvent.getStatus().equals("SUCCESS")){
                notificationService.sendTransferNotification(notification, transferEvent.getBalance(), transferEvent.getAmount(), transferEvent.getTransactionId(), transferEvent.getBeneficiaryAccountNumber());
            }

        }  catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }

}
