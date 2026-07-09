package com.uti.svcreservations.client;

import com.uti.svcreservations.dto.RoomAvailabilityResponse;
import com.uti.svcreservations.dto.RoomResponse;
import com.uti.svcreservations.exception.RoomsServiceException;
import com.uti.svcreservations.exception.ResourceNotfoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class RoomsRestTemplateClient {

    private final RestTemplate restTemplate;

    @Value("${rooms.service.url:http://localhost:8081}")
    private String roomsServiceUrl;

    public RoomsRestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RoomAvailabilityResponse getRoomAvailability(Long roomId) {
        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId + "/availability";
        log.info("RestTemplate - llamando rooms-service: GET {}", url);
        try {
            ResponseEntity<RoomAvailabilityResponse> response = restTemplate.getForEntity(
                    url, RoomAvailabilityResponse.class);
            log.info("RestTemplate - Estado de la respuesta: {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotfoundException("Habitación no encontrada en el rooms-service con id: " + roomId);
            }
            throw new RoomsServiceException("Error del cliente al llamar el rooms-service: " + ex.getMessage(), ex);
        } catch (ResourceAccessException ex) {
            log.error("RestTemplate - no se logro conectar con rooms-service: {}", ex.getMessage());
            throw new RoomsServiceException("No se logro conectar a rooms-service: " + ex.getMessage(), ex);
        }
    }

    public RoomResponse getRoomById(Long roomId) {
        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId;
        log.info("RestTemplate - llamando rooms-service: GET {}", url);
        try {
            ResponseEntity<RoomResponse> response = restTemplate.getForEntity(
                    url, RoomResponse.class);
            log.info("RestTemplate - Estado de la respuesta: {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotfoundException("Habitación no encontrada en el rooms-service con id: " + roomId);
            }
            throw new RoomsServiceException("Error del cliente al llamar el rooms-service: " + ex.getMessage(), ex);
        } catch (ResourceAccessException ex) {
            log.error("RestTemplate - no se logro conectar con rooms-service: {}", ex.getMessage());
            throw new RoomsServiceException("No se logro conectar a rooms-service: " + ex.getMessage(), ex);
        }
    }
}