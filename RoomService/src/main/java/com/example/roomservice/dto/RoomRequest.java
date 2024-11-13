package com.example.roomservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRequest {
    private String roomName;
    private int capacity;
    private String features;

    @JsonProperty("isAvailable")
    private boolean isAvailable;
}
