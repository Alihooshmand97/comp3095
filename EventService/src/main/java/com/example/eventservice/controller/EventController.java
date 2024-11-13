package com.example.eventservice.controller;

import com.example.eventservice.dto.EventRequest;
import com.example.eventservice.dto.EventResponse;
import com.example.eventservice.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest eventRequest) {
        EventResponse eventResponse = eventService.createEvent(eventRequest);
        if ("User not authorized to create this event".equals(eventResponse.getStatusMessage())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(eventResponse);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable String eventId) {
        EventResponse eventResponse = eventService.getEventById(eventId);
        if ("Event not found".equals(eventResponse.getStatusMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eventResponse);
        }
        return ResponseEntity.ok(eventResponse);
    }


    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> eventResponses = eventService.getAllEvents();
        return ResponseEntity.ok(eventResponses);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable String eventId,
            @RequestBody EventRequest eventRequest) {
        EventResponse eventResponse = eventService.updateEvent(eventId, eventRequest);
        if ("Event not found".equals(eventResponse.getStatusMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eventResponse);
        }
        return ResponseEntity.ok(eventResponse);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable String eventId) {
        boolean deleted = eventService.deleteEvent(eventId);
        if (deleted) {
            return ResponseEntity.ok("Event deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }
    }
}
