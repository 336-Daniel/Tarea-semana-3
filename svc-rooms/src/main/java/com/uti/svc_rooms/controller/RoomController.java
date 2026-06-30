package com.uti.svc_rooms.controller;

import com.uti.svc_rooms.dto.AvailabilityResponse;
import com.uti.svc_rooms.dto.RoomRequest;
import com.uti.svc_rooms.dto.RoomResponse;
import com.uti.svc_rooms.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        log.info("GET /api/v1/rooms - Obteniendo todas las habitaciones");
        return ResponseEntity.ok(roomService.getAllRooms());
    }


    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        log.info("GET /api/v1/rooms/{} - Obteniendo habitación por id", id);
        return ResponseEntity.ok(roomService.getRoomById(id));
    }


    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        log.info("POST /api/v1/rooms - Creando nueva habitación con número: {}", request.getRoomNumber());
        RoomResponse createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }


    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        log.info("PUT /api/v1/rooms/{} - Actualizando habitación", id);
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<RoomResponse> updateAvailability(
            @PathVariable Long id,
            @RequestParam @Min(value = 0, message = "Las habitaciones disponibles no pueden ser negativas") Integer availableRooms) {
        log.info("PATCH /api/v1/rooms/{}/availability - Actualizando disponibilidad a: {}", id, availableRooms);
        return ResponseEntity.ok(roomService.updateAvailability(id, availableRooms));
    }


    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(@PathVariable Long id) {
        log.info("GET /api/v1/rooms/{}/availability - Verificando disponibilidad", id);
        return ResponseEntity.ok(roomService.checkAvailability(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        log.info("DELETE /api/v1/rooms/{} - Eliminando habitación", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}