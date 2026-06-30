package com.uti.svc_rooms.mapper;

import com.uti.svc_rooms.dto.RoomRequest;
import com.uti.svc_rooms.dto.RoomResponse;
import com.uti.svc_rooms.model.Room;
import org.springframework.stereotype.Component;


@Component
public class RoomMapper {


    public Room toEntity(RoomRequest request) {
        if (request == null) {
            return null;
        }

        return Room.builder()
                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .pricePerNight(request.getPricePerNight())
                .totalCapacity(request.getTotalCapacity())
                .availableRooms(request.getAvailableRooms())
                .floor(request.getFloor())
                .description(request.getDescription())
                .build();
    }


    public RoomResponse toResponse(Room room) {
        if (room == null) {
            return null;
        }

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType())
                .pricePerNight(room.getPricePerNight())
                .totalCapacity(room.getTotalCapacity())
                .availableRooms(room.getAvailableRooms())
                .floor(room.getFloor())
                .description(room.getDescription())
                .build();
    }


    public void updateEntityFromRequest(RoomRequest request, Room room) {
        if (request == null) {
            return;
        }

        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setTotalCapacity(request.getTotalCapacity());
        room.setAvailableRooms(request.getAvailableRooms());
        room.setFloor(request.getFloor());
        room.setDescription(request.getDescription());
    }
}