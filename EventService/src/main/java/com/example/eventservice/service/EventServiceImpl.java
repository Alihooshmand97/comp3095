package com.example.eventservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.eventservice.dto.EventRequest;
import com.example.eventservice.dto.EventResponse;
import com.example.eventservice.model.Event;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EventServiceImpl implements EventService {

    private static final String USER_SERVICE_URL = "http://userservice:8089/users";

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean verifyUserRole(Long id) {
        String url = USER_SERVICE_URL + "/" + id + "/role";
        String role = restTemplate.getForObject(url, String.class);

        System.out.println("Fetched role for user " + id + ": " + role);

        return "ORGANIZER".equals(role) || "ADMIN".equals(role);
    }

    @Override
    public EventResponse createEvent(EventRequest eventRequest) {
        if (!verifyUserRole(eventRequest.getOrganizerId())) {
            return new EventResponse(null, eventRequest.getEventName(), eventRequest.getOrganizerId(),
                    eventRequest.getEventType(), eventRequest.getExpectedAttendees(),
                    "User not authorized to create this event");
        }

        Event event = new Event();
        event.setEventName(eventRequest.getEventName());
        event.setOrganizerId(eventRequest.getOrganizerId());
        event.setEventType(eventRequest.getEventType());
        event.setExpectedAttendees(eventRequest.getExpectedAttendees());

        Event savedEvent = eventRepository.save(event);

        return new EventResponse(
                savedEvent.getId(),
                savedEvent.getEventName(),
                savedEvent.getOrganizerId(),
                savedEvent.getEventType(),
                savedEvent.getExpectedAttendees(),
                "Event created successfully"
        );
    }

    @Override
    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(event -> new EventResponse(
                        event.getId(),
                        event.getEventName(),
                        event.getOrganizerId(),
                        event.getEventType(),
                        event.getExpectedAttendees(),
                        "Event retrieved successfully"
                ))
                .collect(Collectors.toList());
    }


    @Override
    public EventResponse updateEvent(String eventId, EventRequest eventRequest) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setEventName(eventRequest.getEventName());
            event.setOrganizerId(eventRequest.getOrganizerId());
            event.setEventType(eventRequest.getEventType());
            event.setExpectedAttendees(eventRequest.getExpectedAttendees());

            Event updatedEvent = eventRepository.save(event);
            return new EventResponse(
                    updatedEvent.getId(),
                    updatedEvent.getEventName(),
                    updatedEvent.getOrganizerId(),
                    updatedEvent.getEventType(),
                    updatedEvent.getExpectedAttendees(),
                    "Event updated successfully"
            );
        } else {
            return new EventResponse(null, null, null, null, 0, "Event not found");
        }
    }

    @Override
    public boolean deleteEvent(String eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            eventRepository.deleteById(eventId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public EventResponse getEventById(String eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            return new EventResponse(
                    event.getId(),
                    event.getEventName(),
                    event.getOrganizerId(),
                    event.getEventType(),
                    event.getExpectedAttendees(),
                    "Event retrieved successfully");
        } else {
            return new EventResponse(
                    null,
                    null,
                    null,
                    null,
                    0,
                    "Event not found");
        }
    }
}
