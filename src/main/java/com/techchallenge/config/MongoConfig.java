package com.techchallenge.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@Configuration
@EnableMongoRepositories(basePackages = "com.techchallenge.infrastructure.persistence.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    public static final String MONGODB = "mongodb://";

    @Value(value = "${spring.data.mongodb.host}")
    public String uri;

    @Value(value = "${spring.data.mongodb.port}")
    public String port;

    @Value(value = "${spring.data.mongodb.username}")
    public String user;

    @Value(value = "${spring.data.mongodb.password}")
    public String password;

    @Value(value = "${spring.data.mongodb.database}")
    public String database;

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {

        final ConnectionString connectionString =
                new ConnectionString(getConnectionString());
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    public String getConnectionString(){

        if(StringUtils.isBlank(user) && StringUtils.isBlank(password)){
             return String.format("%s%s:%s", MONGODB, uri, port );
        }
        return String.format("%s%s:%s@%s:%s", MONGODB, user, password, uri, port );
    }
}
