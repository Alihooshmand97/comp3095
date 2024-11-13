package com.example.eventservice.service;

import com.example.eventservice.dto.EventRequest;
import com.example.eventservice.dto.EventResponse;

import java.util.List;

public interface EventService {
    boolean verifyUserRole(Long userId);
    EventResponse createEvent(EventRequest eventRequest);
    List<EventResponse> getAllEvents();
    EventResponse updateEvent(String eventId, EventRequest eventRequest);
    boolean deleteEvent(String eventId);
    EventResponse getEventById(String eventId);
}
