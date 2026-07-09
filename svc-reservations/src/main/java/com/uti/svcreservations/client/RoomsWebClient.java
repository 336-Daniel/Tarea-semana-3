package com.uti.svcreservations.client;

import com.uti.svcreservations.dto.RoomAvailabilityResponse;
import com.uti.svcreservations.dto.RoomResponse;
import com.uti.svcreservations.exception.RoomsServiceException;
import com.uti.svcreservations.exception.ResourceNotfoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Slf4j
public class RoomsWebClient {

    private final WebClient webClient;

    public RoomsWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public RoomAvailabilityResponse getRoomAvailability(Long roomId) {
        log.info("WebClient - llamando rooms-service: GET {/api/v1/rooms/{}/availability}", roomId);
        try {
            return webClient
                    .get()
                    .uri("/api/v1/rooms/{roomId}/availability", roomId)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new ResourceNotfoundException(
                                            "Habitación no encontrada en el rooms-service con id: " + roomId
                                    ))
                    )
                    .onStatus(
                            status -> status.is4xxClientError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new RoomsServiceException(
                                            "Error de cliente desde rooms-service: " + body
                                    ))
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new RoomsServiceException(
                                            "Error de servidor desde rooms-service: " + body
                                    ))
                    )
                    .bodyToMono(RoomAvailabilityResponse.class)
                    .block();

        } catch (WebClientResponseException ex) {
            log.error("WebClient - Error HTTP desde rooms-service: {} {}", ex.getStatusCode(), ex.getMessage());
            throw new RoomsServiceException(
                    "Error al llamar el rooms-service: " + ex.getMessage(), ex);

        } catch (Exception ex) {
            log.error("WebClient - no se logro conectar con rooms-service: {}", ex.getMessage());
            throw new RoomsServiceException(
                    "No se logro conectar a rooms-service: " + ex.getMessage(), ex);
        }
    }

    public RoomResponse getRoomById(Long roomId) {
        log.info("WebClient - llamando rooms-service: GET {/api/v1/rooms/{}}", roomId);
        try {
            return webClient
                    .get()
                    .uri("/api/v1/rooms/{roomId}", roomId)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new ResourceNotfoundException(
                                            "Habitación no encontrada en el rooms-service con id: " + roomId
                                    ))
                    )
                    .onStatus(
                            status -> status.is4xxClientError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new RoomsServiceException(
                                            "Error de cliente desde rooms-service: " + body
                                    ))
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new RoomsServiceException(
                                            "Error de cliente desde rooms-service: " + body
                                    ))
                    )
                    .bodyToMono(RoomResponse.class)
                    .block();

        } catch (WebClientResponseException ex) {
            log.error("WebClient - Error HTTP desde rooms-service: {} {}", ex.getStatusCode(), ex.getMessage());
            throw new RoomsServiceException(
                    "Error del cliente al llamar el rooms-service: " + ex.getMessage(), ex);

        } catch (Exception ex) {
            log.error("WebClient - no se logro conectar con rooms-service: {}", ex.getMessage());
            throw new RoomsServiceException(
                    "No se logro conectar a rooms-service: " + ex.getMessage(), ex);
        }
    }
}