package com.example.bookingservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;
    private String userId;
    private String roomId;
    private String startTime;
    private String endTime;
    private String purpose;
    private String status;

}
