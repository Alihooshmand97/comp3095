package com.example.roomservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.roomservice.config.TestContainerConfig;
import com.example.roomservice.dto.RoomRequest;
import com.example.roomservice.dto.RoomResponse;
import com.example.roomservice.repository.RoomRepository;
import com.example.roomservice.service.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(TestContainerConfig.class)
public class RoomServiceApplicationTests {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomServiceImpl roomService;

    @BeforeEach
    public void setup() {
        roomRepository.deleteAll();
    }

    @Test
    public void testCreateRoom() {
        RoomRequest roomRequest = new RoomRequest(
                "Deluxe Room",
                2,
                "Wi-Fi, TV",
                true);

        RoomResponse roomResponse = roomService.createRoom(roomRequest);

        assertEquals("Deluxe Room", roomResponse.getRoomName());
        assertEquals(2, roomResponse.getCapacity());
        assertTrue(roomResponse.isAvailable());
    }

    @Test
    public void testGetAllRooms() {
        RoomRequest roomRequest1 = new RoomRequest(
                "Deluxe Room",
                2,
                "Wi-Fi, TV",
                true);
        RoomRequest roomRequest2 = new RoomRequest(
                "Standard Room",
                3,
                "Wi-Fi",
                false);
        roomService.createRoom(roomRequest1);
        roomService.createRoom(roomRequest2);

        var rooms = roomService.getAllRooms();

        assertEquals(2, rooms.size());
    }

    @Test
    public void testGetRoomById() {
        RoomRequest roomRequest = new RoomRequest(
                "Deluxe Room",
                2,
                "Wi-Fi, TV",
                true);
        RoomResponse roomResponse = roomService.createRoom(roomRequest);

        RoomResponse fetchedRoom = roomService.getRoomById(roomResponse.getId());

        assertEquals(roomResponse.getId(), fetchedRoom.getId());
        assertEquals("Deluxe Room", fetchedRoom.getRoomName());
    }

    @Test
    public void testDeleteRoom() {
        RoomRequest roomRequest = new RoomRequest(
                "Deluxe Room",
                2,
                "Wi-Fi, TV",
                true);
        RoomResponse roomResponse = roomService.createRoom(roomRequest);


        roomService.deleteRoom(roomResponse.getId());

        try {
            roomService.getRoomById(roomResponse.getId());
        } catch (RuntimeException e) {
            assertEquals("Room not found with id: " + roomResponse.getId(), e.getMessage());
        }
    }

}
