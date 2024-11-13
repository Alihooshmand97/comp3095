package com.example.eventservice;

import com.example.eventservice.dto.EventRequest;
import com.example.eventservice.dto.EventResponse;
import com.example.eventservice.model.Event;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.service.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private EventServiceImpl eventService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void verifyUserRole_WhenOrganizerRole_ShouldReturnTrue() {
		Long organizerId = 1L;
		String url = "http://userservice:8089/users/" + organizerId + "/role";

		when(restTemplate.getForObject(url, String.class)).thenReturn("ORGANIZER");

		assertTrue(eventService.verifyUserRole(organizerId));
	}

	@Test
	void verifyUserRole_WhenNonAuthorizedRole_ShouldReturnFalse() {
		Long userId = 2L;
		String url = "http://userservice:8089/users/" + userId + "/role";

		when(restTemplate.getForObject(url, String.class)).thenReturn("USER");

		assertFalse(eventService.verifyUserRole(userId));
	}

	@Test
	void createEvent_WithAuthorizedUser_ShouldCreateEvent() {
		Long organizerId = 1L;
		EventRequest eventRequest = new EventRequest("Tech Conference", organizerId, "Conference", 100);
		String url = "http://userservice:8089/users/" + organizerId + "/role";

		when(restTemplate.getForObject(url, String.class)).thenReturn("ORGANIZER");

		Event event = new Event();
		event.setId("123");
		event.setEventName(eventRequest.getEventName());
		event.setOrganizerId(eventRequest.getOrganizerId());
		event.setEventType(eventRequest.getEventType());
		event.setExpectedAttendees(eventRequest.getExpectedAttendees());

		when(eventRepository.save(any(Event.class))).thenReturn(event);

		EventResponse response = eventService.createEvent(eventRequest);

		assertNotNull(response);
		assertEquals("123", response.getId());
		assertEquals("Tech Conference", response.getEventName());
		assertEquals("Event created successfully", response.getStatusMessage());
	}

	@Test
	void createEvent_WithUnauthorizedUser_ShouldNotCreateEvent() {
		Long userId = 2L;
		EventRequest eventRequest = new EventRequest("Tech Conference", userId, "Conference", 100);
		String url = "http://userservice:8089/users/" + userId + "/role";

		when(restTemplate.getForObject(url, String.class)).thenReturn("USER");

		EventResponse response = eventService.createEvent(eventRequest);

		assertNull(response.getId());
		assertEquals("User not authorized to create this event", response.getStatusMessage());
	}

	@Test
	void getAllEvents_ShouldReturnListOfEvents() {
		Event event1 = new Event("1", "Tech Conference", 1L, "Conference", 100);
		Event event2 = new Event("2", "Music Festival", 2L, "Festival", 500);

		when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));

		List<EventResponse> events = eventService.getAllEvents();

		assertEquals(2, events.size());
		assertEquals("Tech Conference", events.get(0).getEventName());
		assertEquals("Music Festival", events.get(1).getEventName());
	}

	@Test
	void updateEvent_WithExistingEvent_ShouldUpdateEvent() {
		String eventId = "123";
		EventRequest eventRequest = new EventRequest("Updated Conference", 1L, "Conference", 150);
		Event existingEvent = new Event(eventId, "Old Conference", 1L, "Conference", 100);

		when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
		when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

		EventResponse response = eventService.updateEvent(eventId, eventRequest);

		assertNotNull(response);
		assertEquals("Updated Conference", response.getEventName());
		assertEquals("Event updated successfully", response.getStatusMessage());
	}

	@Test
	void updateEvent_WithNonExistingEvent_ShouldReturnNotFound() {
		String eventId = "999";
		EventRequest eventRequest = new EventRequest("Updated Conference", 1L, "Conference", 150);

		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		EventResponse response = eventService.updateEvent(eventId, eventRequest);

		assertNull(response.getId());
		assertEquals("Event not found", response.getStatusMessage());
	}

	@Test
	void deleteEvent_WithExistingEvent_ShouldReturnTrue() {
		String eventId = "123";
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(new Event()));

		boolean isDeleted = eventService.deleteEvent(eventId);

		assertTrue(isDeleted);
		verify(eventRepository, times(1)).deleteById(eventId);
	}

	@Test
	void deleteEvent_WithNonExistingEvent_ShouldReturnFalse() {
		String eventId = "999";
		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		boolean isDeleted = eventService.deleteEvent(eventId);

		assertFalse(isDeleted);
		verify(eventRepository, never()).deleteById(eventId);
	}

	@Test
	void getEventById_WithExistingEvent_ShouldReturnEvent() {
		String eventId = "123";
		Event event = new Event(eventId, "Tech Conference", 1L, "Conference", 100);

		when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

		EventResponse response = eventService.getEventById(eventId);

		assertNotNull(response);
		assertEquals("Tech Conference", response.getEventName());
		assertEquals("Event retrieved successfully", response.getStatusMessage());
	}

	@Test
	void getEventById_WithNonExistingEvent_ShouldReturnNotFound() {
		String eventId = "999";
		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		EventResponse response = eventService.getEventById(eventId);

		assertNull(response.getId());
		assertEquals("Event not found", response.getStatusMessage());
	}
}
