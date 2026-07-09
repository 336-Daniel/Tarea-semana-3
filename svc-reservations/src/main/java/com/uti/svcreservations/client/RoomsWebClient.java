package com.uti.svcreservations.client;

import com.uti.svcreservations.dto.RoomResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class RoomsWebClient {

    private final WebClient webClient;

    @Value("${rooms.service.url:http://localhost:8081}")
    private String roomsServiceUrl;


    @CircuitBreaker(name = "catalogService", fallbackMethod = "getRoomByIdAsyncFallback")
    @Retry(name = "catalogService")
    public Mono<RoomResponse> getRoomByIdAsync(Long roomId) {
        log.info("Obteniendo detalles de habitación {} mediante WebClient (asíncrono)", roomId);

        return webClient
                .get()
                .uri(roomsServiceUrl + "/api/v1/rooms/{id}", roomId)
                .retrieve()
                .bodyToMono(RoomResponse.class)
                .doOnNext(room -> log.info("Detalles de habitación obtenidos: {}", room))
                .doOnError(error -> log.error("Error al obtener detalles de habitación {}: {}",
                        roomId, error.getMessage()));
    }


    @CircuitBreaker(name = "catalogService", fallbackMethod = "getRoomByIdFallback")
    @Retry(name = "catalogService")
    public RoomResponse getRoomById(Long roomId) {
        log.info("Obteniendo detalles de habitación {} mediante WebClient (síncrono)", roomId);

        return webClient
                .get()
                .uri(roomsServiceUrl + "/api/v1/rooms/{id}", roomId)
                .retrieve()
                .bodyToMono(RoomResponse.class)
                .doOnNext(room -> log.info("Detalles de habitación obtenidos: {}", room))
                .doOnError(error -> log.error("Error al obtener detalles de habitación {}: {}",
                        roomId, error.getMessage()))
                .block(); // Bloquea para obtener resultado síncrono
    }


    public Mono<RoomResponse> getRoomByIdAsyncFallback(Long roomId, Throwable throwable) {
        log.warn("Fallback activado para getRoomByIdAsync. Razón: {}", throwable.getMessage());
        return Mono.just(
                RoomResponse.builder()
                        .id(roomId)
                        .roomNumber("Información no disponible")
                        .type("N/A")
                        .pricePerNight(0.0)
                        .totalCapacity(0)
                        .availableRooms(0)
                        .description("Servicio temporalmente no disponible")
                        .build()
        );
    }


    public RoomResponse getRoomByIdFallback(Long roomId, Throwable throwable) {
        log.warn("Fallback activado para getRoomById (WebClient). Razón: {}", throwable.getMessage());
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