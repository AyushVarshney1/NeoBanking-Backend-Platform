package com.neobank.neobankaccountservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic accountCreatedTopic(){
        return TopicBuilder.name("account.created.v1").build();
    }

    @Bean
    public NewTopic accountStatusUpdatedTopic(){
        return TopicBuilder.name("account.status.updated.v1").build();
    }

    @Bean
    public NewTopic accountKycCompletedTopic(){
        return TopicBuilder.name("account.kyc.completed.v1").build();
    }
}
