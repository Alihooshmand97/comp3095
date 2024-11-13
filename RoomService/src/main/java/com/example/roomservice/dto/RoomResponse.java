package com.example.roomservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {
    private Long id;
    private String roomName;
    private int capacity;
    private String features;

    @JsonProperty("isAvailable")
    private boolean isAvailable;
}