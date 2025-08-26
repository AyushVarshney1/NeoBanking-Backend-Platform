package com.neobank.neobanknotificationservice.service;

import com.neobank.neobanknotificationservice.model.Notification;
import com.neobank.neobanknotificationservice.model.enums.NotificationStatus;
import com.neobank.neobanknotificationservice.model.enums.NotificationType;
import com.neobank.neobanknotificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendUserCreatedNotification(Notification notification) {
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(notification);

        try {
            Context context = new Context();
            context.setVariable("userEmail", notification.getEmail());
            String htmlContent = templateEngine.process("user-signup-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(notification.getEmail());
            helper.setSubject("🎉 Welcome to NeoBank!");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error(e.getMessage());
        }

        notificationRepository.save(notification);
    }

    @Async
    public void sendAccountCreatedNotification(Notification notification) {
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(notification);

        try {
            Context context = new Context();
            context.setVariable("userEmail", notification.getEmail());
            context.setVariable("accountNumber", notification.getAccountNumber());
            String htmlContent = templateEngine.process("account-created-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(notification.getEmail());
            helper.setSubject("🎉 Account Created Successfully!");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error(e.getMessage());
        }

        notificationRepository.save(notification);
    }

    @Async
    public void sendAccountStatusUpdatedNotification(Notification notification) {
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(notification);

        try {
            Context context = new Context();

            if(notification.getType().equals(NotificationType.ACCOUNT_ACTIVATED)){
                context.setVariable("status", "Activated");
                context.setVariable("statusColor", "#27AE60");
            }
            else{
                context.setVariable("status", "Deactivated");
                context.setVariable("statusColor", "#C0392B");
            }

            context.setVariable("userEmail", notification.getEmail());
            context.setVariable("accountNumber", notification.getAccountNumber());
            String htmlContent = templateEngine.process("account-status-updated-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(notification.getEmail());
            if(notification.getType().equals(NotificationType.ACCOUNT_ACTIVATED)){
                helper.setSubject("Account Activated");
            }
            else{
                helper.setSubject("Account Deactivated");
            }
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error(e.getMessage());
        }

        notificationRepository.save(notification);
    }

    @Async
    public void sendKycCompletedNotification(Notification notification) {
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(notification);

        try {
            Context context = new Context();
            context.setVariable("userEmail", notification.getEmail());
            context.setVariable("accountNumber", notification.getAccountNumber());
            String htmlContent = templateEngine.process("kyc-completed-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(notification.getEmail());
            helper.setSubject("🎉 KYC Completed Successfully!");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error(e.getMessage());
        }

        notificationRepository.save(notification);
    }

    @Async
    public void sendBalanceUpdatedNotification(Notification notification, double balance, double amount, String transactionId) {
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(notification);

        try {
            Context context = new Context();
            context.setVariable("userEmail", notification.getEmail());
            context.setVariable("accountNumber", notification.getAccountNumber());
            context.setVariable("transactionType", notification.getType().equals(NotificationType.TRANSACTION_DEPOSITED) ? "Deposit" : "Withdrawal");
            context.setVariable("balance", balance);
            context.setVariable("amount", amount);
            context.setVariable("transactionId", transactionId);
            String htmlContent = templateEngine.process("balance-updated-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(notification.getEmail());
            if(notification.getType().equals(NotificationType.TRANSACTION_DEPOSITED)){
                helper.setSubject("Amount Deposited Successfully!");
            }
            else{
                helper.setSubject("Amount Withdrawn Successfully!");
            }
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error(e.getMessage());
        }

        notificationRepository.save(notification);
    }

    @Async
    public void sendTransferNotification(Notification notification, double balance, double amount, String transactionId, Long beneficiaryAccountNumber) {
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(notification);

        try {
            Context context = new Context();
            context.setVariable("userEmail", notification.getEmail());
            context.setVariable("accountNumber", notification.getAccountNumber());
            context.setVariable("beneficiaryAccountNumber", beneficiaryAccountNumber);
            context.setVariable("balance", balance);
            context.setVariable("amount", amount);
            context.setVariable("transactionId", transactionId);
            String htmlContent = templateEngine.process("transfer-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(notification.getEmail());
            helper.setSubject("Amount Transferred Successfully!");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error(e.getMessage());
        }

        notificationRepository.save(notification);
    }
}



