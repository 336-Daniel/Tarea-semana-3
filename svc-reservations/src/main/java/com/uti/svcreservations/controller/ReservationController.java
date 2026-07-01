package com.uti.svcreservations.controller;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.model.Status;
import com.uti.svcreservations.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        log.info("GET /api/v1/reservations - Obteniendo todas las reservaciones");
        return ResponseEntity.ok(reservationService.getReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        log.info("GET /api/v1/reservations/{} - Obteniendo reservacion por id: ", id);
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        log.info("POST /api/v1/reservations - reserva creada para la habitación con id: {}", request.getRoomId());
        ReservationResponse createReservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createReservation);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ReservationService> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationRequest request) {
//        log.info("PUT /api/v1/reservations/{} - reserva ACTUALIZADA ", id);
//        return ResponseEntity.ok(reservationService)


    @PatchMapping("/{id}/checkout")
    public ResponseEntity<ReservationResponse> checkout(@PathVariable Long id) {
        log.info("PATCH /api/v1/reservations/{}/checkout - status cambiado a COMPLETED", id);
        return ResponseEntity.ok(reservationService.checkout(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.info("DELETE /api/v1/reservations/{} - reserva ELIMINADA ", id);
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();


    }

    @GetMapping("/guest/{email}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByEmail(@PathVariable String email) {
        log.info("GET /api/v1/reservations/guest/{} - Obteniendo reservacion por email: {} ", email,email);
        return ResponseEntity.ok(reservationService.getReservationsByEmail(email));
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByStatus(@PathVariable Status status) {
        log.info("GET /api/v1/reservations/status/{} - Obteniendo reservacion por status:  ", status);
        return ResponseEntity.ok(reservationService.getReservationsByStatus(status));

    }


}
