package com.neobank.neobanknotificationservice.model;

import com.neobank.neobanknotificationservice.model.enums.NotificationStatus;
import com.neobank.neobanknotificationservice.model.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String userId;
    private Long accountNumber;
    private String email;

    private NotificationType type;
    private NotificationStatus status;

    @CreatedDate
    private Instant createdAt;
}
