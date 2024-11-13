package com.example.approvalservice.controller;

import com.example.approvalservice.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;


    @PostMapping("/approve")
    public ResponseEntity<String> approveEvent(@RequestParam String eventId, @RequestParam Long approverId) {
        return approvalService.processApproval(eventId, approverId);
    }
}
