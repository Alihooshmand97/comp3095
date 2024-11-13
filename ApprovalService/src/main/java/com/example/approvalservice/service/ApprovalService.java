package com.example.approvalservice.service;

import com.example.approvalservice.dto.EventDTO;
import com.example.approvalservice.dto.UserDTO;
import com.example.approvalservice.model.Approval;
import com.example.approvalservice.repository.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApprovalService {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${eventservice.url}")
    private String eventServiceUrl;

    @Value("${userservice.url}")
    private String userServiceUrl;

    public ResponseEntity<String> processApproval(String eventId, Long approverId) {
            ResponseEntity<EventDTO> eventResponse = restTemplate.getForEntity(eventServiceUrl + "/events/" + eventId, EventDTO.class);
            if (!eventResponse.getStatusCode().is2xxSuccessful() || eventResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
            }
            EventDTO event = eventResponse.getBody();

            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(userServiceUrl + "/users/" + approverId, UserDTO.class);
            if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to approve");
            }

            UserDTO approver = userResponse.getBody();
            if (approver == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized to approve");
            }

            if (!"ADMIN".equalsIgnoreCase(approver.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admin can approve events");
            }

            Approval approval = new Approval(eventId, approverId, true, "Approved");
            approvalRepository.save(approval);

            return ResponseEntity.ok("Event approved successfully");

    }
}