package com.example.approvalservice.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "approvals")
public class Approval {
    @Id
    private String id;
    private String eventId;
    private Long approverId;
    private boolean approved;
    private String comments;


    public Approval(String eventId, Long approverId, boolean approved, String comments) {
        this.eventId = eventId;
        this.approverId = approverId;
        this.approved = approved;
        this.comments = comments;
    }

}