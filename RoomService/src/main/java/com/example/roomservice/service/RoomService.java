package com.example.roomservice.service;

import com.example.roomservice.dto.RoomRequest;
import com.example.roomservice.dto.RoomResponse;

import java.util.List;


public interface RoomService {
    RoomResponse createRoom(RoomRequest roomRequest);
    List<RoomResponse> getAllRooms();
    RoomResponse getRoomById(Long id);
    RoomResponse updateRoom(Long id, RoomRequest roomRequest);
    void deleteRoom(Long id);

    List<RoomResponse> getAvailableRooms();

    boolean isRoomAvailable(Long roomId);

    RoomResponse setRoomAvailability(Long roomId, boolean availability);
}
