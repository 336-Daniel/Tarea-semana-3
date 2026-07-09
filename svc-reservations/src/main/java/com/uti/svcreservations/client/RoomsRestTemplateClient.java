package com.uti.svcreservations.client;

import com.uti.svcreservations.dto.RoomAvailabilityResponse;
import com.uti.svcreservations.dto.RoomResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
@Slf4j
public class RoomsRestTemplateClient {

    private final RestTemplate restTemplate;

    @Value("${rooms.service.url:http://localhost:8081}")
    private String roomsServiceUrl;


    @CircuitBreaker(name = "catalogService", fallbackMethod = "getRoomAvailabilityFallback")
    @Retry(name = "catalogService")
    public RoomAvailabilityResponse getRoomAvailability(Long roomId) {
        log.info("Consultando disponibilidad de habitación {} mediante RestTemplate", roomId);

        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId + "/availability";

        try {
            RoomAvailabilityResponse response = restTemplate.getForObject(
                    url,
                    RoomAvailabilityResponse.class
            );
            log.info("Disponibilidad obtenida: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error al obtener disponibilidad de habitación {}: {}", roomId, e.getMessage());
            throw e;
        }
    }


    @CircuitBreaker(name = "catalogService", fallbackMethod = "getRoomByIdFallback")
    @Retry(name = "catalogService")
    public RoomResponse getRoomById(Long roomId) {
        log.info("Obteniendo detalles de habitación {} mediante RestTemplate", roomId);

        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId;

        try {
            RoomResponse response = restTemplate.getForObject(
                    url,
                    RoomResponse.class
            );
            log.info("Detalles de habitación obtenidos: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error al obtener detalles de habitación {}: {}", roomId, e.getMessage());
            throw e;
        }
    }


    public RoomAvailabilityResponse getRoomAvailabilityFallback(Long roomId, Throwable throwable) {
        log.warn("Fallback activado para getRoomAvailability. Razón: {}", throwable.getMessage());
        return RoomAvailabilityResponse.builder()
                .roomId(roomId)
                .available(false)
                .availableRooms(0)
                .build();
    }


    public RoomResponse getRoomByIdFallback(Long roomId, Throwable throwable) {
        log.warn("Fallback activado para getRoomById. Razón: {}", throwable.getMessage());
        return RoomResponse.builder()
                .id(roomId)
                .roomNumber("Información no disponible")
                .type("N/A")
                .pricePerNight(0.0)
                .totalCapacity(0)
                .availableRooms(0)
                .description("Servicio temporalmente no disponible")
                .build();
    }
}
