package com.techchallenge.infrastructure.persistence.repository;

import com.mongodb.client.MongoCollection;
import com.techchallenge.infrastructure.persistence.documents.PaymentDocument;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PaymentCollection  {
    private MongoTemplate mongoTemplate;

    public PaymentCollection(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<PaymentDocument> findPaymentExpired(LocalDateTime now){
        Criteria criteria = Criteria.where("sent").is(false).and("expirationDate").lte(now);
        Query query = new Query().addCriteria(criteria);
        query.with(Sort.by(Sort.Direction.ASC, "createTime"));
        return mongoTemplate.find(query, PaymentDocument.class);
    }
}
