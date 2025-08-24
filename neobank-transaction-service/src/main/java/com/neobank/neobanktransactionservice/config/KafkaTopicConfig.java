package com.neobank.neobanktransactionservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic balanceUpdateTopic(){
        return TopicBuilder.name("transaction.balance.updated.v1").build();
    }

    @Bean NewTopic accountTransferTopic(){
        return TopicBuilder.name("transaction.amount.transferred.v1").build();
    }


}
