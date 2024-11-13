package com.example.approvalservice.repository;

import com.example.approvalservice.model.Approval;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApprovalRepository extends MongoRepository<Approval, String> {

}
