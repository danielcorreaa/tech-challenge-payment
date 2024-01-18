package com.techchallenge.infrastructure.persistence.repository;

import com.techchallenge.infrastructure.persistence.documents.PaymentDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface PaymentRepository extends MongoRepository<PaymentDocument, String> {


    @Query("{ 'sent' : false , 'orderStatus': 'paid'}")
    List<PaymentDocument> findNotSendAndIsPaid(Sort createTime);
}
