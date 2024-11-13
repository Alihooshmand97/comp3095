package com.example.roomservice.service;

import com.example.roomservice.dto.RoomRequest;
import com.example.roomservice.dto.RoomResponse;
import com.example.roomservice.model.Room;
import com.example.roomservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public RoomResponse createRoom(RoomRequest roomRequest) {
        Room room = Room.builder()
                .roomName(roomRequest.getRoomName())
                .capacity(roomRequest.getCapacity())
                .features(roomRequest.getFeatures())
                .isAvailable(roomRequest.isAvailable())
                .build();

        Room savedRoom = roomRepository.save(room);
        return new RoomResponse(
                savedRoom.getId(),
                savedRoom.getRoomName(),
                savedRoom.getCapacity(),
                savedRoom.getFeatures(),
                savedRoom.isAvailable());
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        return new RoomResponse(
                room.getId(),
                room.getRoomName(),
                room.getCapacity(),
                room.getFeatures(),
                room.isAvailable());
    }

    @Override
    public RoomResponse updateRoom(Long id, RoomRequest roomRequest) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        room.setRoomName(roomRequest.getRoomName());
        room.setCapacity(roomRequest.getCapacity());
        room.setFeatures(roomRequest.getFeatures());
        room.setAvailable(roomRequest.isAvailable());

        Room updatedRoom = roomRepository.save(room);
        return new RoomResponse(
                updatedRoom.getId(),
                updatedRoom.getRoomName(),
                updatedRoom.getCapacity(),
                updatedRoom.getFeatures(),
                updatedRoom.isAvailable());
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        roomRepository.delete(room);
    }


    public List<RoomResponse> getAvailableRooms() {
        List<Room> availableRooms = roomRepository.findByIsAvailable(true);
        return availableRooms.stream()
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getRoomName(),
                        room.getCapacity(),
                        room.getFeatures(),
                        room.isAvailable()))
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return room.isAvailable();
    }

    @Override
    public RoomResponse setRoomAvailability(Long roomId, boolean availability) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setAvailable(availability);
        Room updatedRoom = roomRepository.save(room);
        return mapToRoomResponse(updatedRoom);
    }

    private RoomResponse mapToRoomResponse(Room room) {
        return new RoomResponse(room.getId(),
                room.getRoomName(),
                room.getCapacity(),
                room.getFeatures(),
                room.isAvailable());
    }

}