package com.neobank.neobanknotificationservice.repository;

import com.neobank.neobanknotificationservice.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {

}
