package com.uti.svcreservations.controller;

import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
