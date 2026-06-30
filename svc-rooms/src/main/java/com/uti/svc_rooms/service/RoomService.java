package com.uti.svc_rooms.service;

import com.uti.svc_rooms.dto.AvailabilityResponse;
import com.uti.svc_rooms.dto.RoomRequest;
import com.uti.svc_rooms.dto.RoomResponse;

import java.util.List;


public interface RoomService {

    List<RoomResponse> getAllRooms();

    RoomResponse getRoomById(Long id);

    RoomResponse createRoom(RoomRequest request);

    RoomResponse updateRoom(Long id, RoomRequest request);

    RoomResponse updateAvailability(Long id, Integer availableRooms);

    AvailabilityResponse checkAvailability(Long id);

    void deleteRoom(Long id);
}
