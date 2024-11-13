package com.example.approvalservice;

import com.example.approvalservice.dto.EventDTO;
import com.example.approvalservice.dto.UserDTO;
import com.example.approvalservice.model.Approval;
import com.example.approvalservice.repository.ApprovalRepository;
import com.example.approvalservice.service.ApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApprovalServiceTest {

	@Mock
	private ApprovalRepository approvalRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private ApprovalService approvalService;

	private static final String EVENT_ID = "event1";
	private static final Long APPROVER_ID = 1L;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testProcessApproval_WhenEventAndUserAreValid() {
		EventDTO eventDTO = new EventDTO();
		eventDTO.setEventName("Sample Event");

		UserDTO userDTO = new UserDTO();
		userDTO.setRole("ADMIN");

		when(restTemplate.getForEntity(anyString(), eq(EventDTO.class)))
				.thenReturn(ResponseEntity.ok(eventDTO));
		when(restTemplate.getForEntity(anyString(), eq(UserDTO.class)))
				.thenReturn(ResponseEntity.ok(userDTO));

		Approval approval = new Approval(EVENT_ID, APPROVER_ID, true, "Approved");
		when(approvalRepository.save(any(Approval.class))).thenReturn(approval);

		ResponseEntity<String> response = approvalService.processApproval(EVENT_ID, APPROVER_ID);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Event approved successfully", response.getBody());
		verify(approvalRepository, times(1)).save(any(Approval.class));
	}

	@Test
	void testProcessApproval_WhenEventNotFound() {
		when(restTemplate.getForEntity(anyString(), eq(EventDTO.class)))
				.thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

		ResponseEntity<String> response = approvalService.processApproval(EVENT_ID, APPROVER_ID);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Event not found", response.getBody());
	}

	@Test
	void testProcessApproval_WhenUserNotAuthorized() {
		EventDTO eventDTO = new EventDTO();
		eventDTO.setEventName("Sample Event");

		when(restTemplate.getForEntity(anyString(), eq(EventDTO.class)))
				.thenReturn(ResponseEntity.ok(eventDTO));

		// Simulating a non-admin user
		UserDTO userDTO = new UserDTO();
		userDTO.setRole("USER");

		when(restTemplate.getForEntity(anyString(), eq(UserDTO.class)))
				.thenReturn(ResponseEntity.ok(userDTO));

		ResponseEntity<String> response = approvalService.processApproval(EVENT_ID, APPROVER_ID);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertEquals("Only Admin can approve events", response.getBody());
	}

	@Test
	void testProcessApproval_WhenUserNotFound() {
		EventDTO eventDTO = new EventDTO();
		eventDTO.setEventName("Sample Event");

		when(restTemplate.getForEntity(anyString(), eq(EventDTO.class)))
				.thenReturn(ResponseEntity.ok(eventDTO));

		when(restTemplate.getForEntity(anyString(), eq(UserDTO.class)))
				.thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

		ResponseEntity<String> response = approvalService.processApproval(EVENT_ID, APPROVER_ID);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertEquals("User not authorized to approve", response.getBody());
	}

	@Test
	void testProcessApproval_WhenEventServiceFails() {
		when(restTemplate.getForEntity(anyString(), eq(EventDTO.class)))
				.thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));

		ResponseEntity<String> response = approvalService.processApproval(EVENT_ID, APPROVER_ID);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Event not found", response.getBody());
	}

	@Test
	void testProcessApproval_WhenUserServiceFails() {
		EventDTO eventDTO = new EventDTO();
		eventDTO.setEventName("Sample Event");

		when(restTemplate.getForEntity(anyString(), eq(EventDTO.class)))
				.thenReturn(ResponseEntity.ok(eventDTO));

		when(restTemplate.getForEntity(anyString(), eq(UserDTO.class)))
				.thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));

		ResponseEntity<String> response = approvalService.processApproval(EVENT_ID, APPROVER_ID);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertEquals("User not authorized to approve", response.getBody());
	}
}
