package com.example.bookingservice;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.BookingResponse;
import com.example.bookingservice.exception.RoomNotAvailableException;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.repository.BookingRepository;
import com.example.bookingservice.service.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking_WhenRoomIsAvailable() {
        BookingRequest request = new BookingRequest();
        request.setRoomId("room1");
        request.setUserId("1");
        request.setPurpose("Meeting");
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(1));

        when(restTemplate.getForObject("http://roomservice:8085/rooms/available/room1", Boolean.class))
                .thenReturn(true);

        Booking booking = new Booking();
        booking.setId("1");
        booking.setRoomId("room1");
        booking.setUserId("1");
        booking.setPurpose("Meeting");
        booking.setStatus("CONFIRMED");

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals("1", response.getBookingId());
        assertEquals("Meeting", response.getPurpose());
        assertEquals("CONFIRMED", response.getStatus());
    }

    @Test
    void testCreateBooking_WhenRoomIsNotAvailable() {
        BookingRequest request = new BookingRequest();
        request.setRoomId("room2");
        request.setUserId("2");
        request.setPurpose("Conference");
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(2));

        when(restTemplate.getForObject("http://roomservice:8085/rooms/available/room2", Boolean.class))
                .thenReturn(false);

        assertThrows(RoomNotAvailableException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void testGetBookingById_Found() {
        Booking booking = new Booking();
        booking.setId("1");
        booking.setRoomId("room1");
        booking.setUserId("1");
        booking.setPurpose("Meeting");
        booking.setStatus("CONFIRMED");

        when(bookingRepository.findById("1")).thenReturn(Optional.of(booking));

        Optional<BookingResponse> response = bookingService.getBookingById("1");

        assertTrue(response.isPresent());
        assertEquals("Meeting", response.get().getPurpose());
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingRepository.findById("999")).thenReturn(Optional.empty());

        Optional<BookingResponse> response = bookingService.getBookingById("999");

        assertFalse(response.isPresent());
    }

    @Test
    void testUpdateBooking() {
        Booking existingBooking = new Booking();
        existingBooking.setId("1");
        existingBooking.setRoomId("room1");
        existingBooking.setUserId("1");
        existingBooking.setPurpose("Meeting");
        existingBooking.setStatus("CONFIRMED");

        when(bookingRepository.findById("1")).thenReturn(Optional.of(existingBooking));

        BookingRequest updateRequest = new BookingRequest();
        updateRequest.setPurpose("Updated Meeting");
        updateRequest.setStartTime(LocalDateTime.now().plusDays(1));
        updateRequest.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.updateBooking("1", updateRequest);

        assertNotNull(response);
        assertEquals("Updated Meeting", response.getPurpose());
    }

    @Test
    void testDeleteBooking_Success() {
        Booking booking = new Booking();
        booking.setId("1");

        when(bookingRepository.findById("1")).thenReturn(Optional.of(booking));

        bookingService.deleteBooking("1");

        verify(bookingRepository, times(1)).delete(booking);
    }

    @Test
    void testDeleteBooking_NotFound() {
        when(bookingRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.deleteBooking("999"));
    }
}
