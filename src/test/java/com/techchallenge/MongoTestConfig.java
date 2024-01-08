package com.techchallenge;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(value = {"com.techchallenge"})
@EnableMongoRepositories
public class MongoTestConfig {
}
