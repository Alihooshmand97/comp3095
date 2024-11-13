package com.example.bookingservice.service;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.BookingResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    List<BookingResponse> getAllBookings();

    BookingResponse createBooking(BookingRequest bookingRequest);

    Optional<BookingResponse> getBookingById(String id);

    BookingResponse updateBooking(String id, BookingRequest bookingDetails);

    void deleteBooking(String id);
}
