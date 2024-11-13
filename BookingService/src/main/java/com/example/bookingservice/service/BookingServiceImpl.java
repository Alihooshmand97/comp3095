package com.example.bookingservice.service;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.BookingResponse;
import com.example.bookingservice.exception.RoomNotAvailableException;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final String ROOM_SERVICE_URL = "http://roomservice:8085/rooms";

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse createBooking(BookingRequest bookingRequest) {
        String url = ROOM_SERVICE_URL + "/available/" + bookingRequest.getRoomId();

        Boolean isAvailable = restTemplate.getForObject(url, Boolean.class);

        if (!Boolean.TRUE.equals(isAvailable)) {
            throw new RoomNotAvailableException("Room is not available for the selected time.");
        }

        Booking booking = new Booking();
        booking.setRoomId(bookingRequest.getRoomId());
        booking.setUserId(bookingRequest.getUserId());
        booking.setPurpose(bookingRequest.getPurpose());
        booking.setStatus("CONFIRMED");

        if (bookingRequest.getStartTime() != null) {
            booking.setStartTime(bookingRequest.getStartTime().format(formatter));
        }
        if (bookingRequest.getEndTime() != null) {
            booking.setEndTime(bookingRequest.getEndTime().format(formatter));
        }

        Booking savedBooking = bookingRepository.save(booking);
        return convertToBookingResponse(savedBooking);
    }

    @Override
    public Optional<BookingResponse> getBookingById(String id) {
        return bookingRepository.findById(id).map(this::convertToBookingResponse);
    }

    @Override
    public BookingResponse updateBooking(String id, BookingRequest bookingDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setStartTime(bookingDetails.getStartTime().format(formatter));
        booking.setEndTime(bookingDetails.getEndTime().format(formatter));
        booking.setPurpose(bookingDetails.getPurpose());

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToBookingResponse(updatedBooking);
    }

    @Override
    public void deleteBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        bookingRepository.delete(booking);
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();

        response.setBookingId(booking.getId().toString());
        response.setRoomId(booking.getRoomId());
        response.setUserId(booking.getUserId());

        if (booking.getStartTime() != null) {
            response.setStartTime(LocalDateTime.parse(booking.getStartTime(), formatter));
        }

        if (booking.getEndTime() != null) {
            response.setEndTime(LocalDateTime.parse(booking.getEndTime(), formatter));
        }

        response.setPurpose(booking.getPurpose());
        response.setStatus(booking.getStatus());

        return response;
    }

}