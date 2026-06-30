package com.uti.svc_rooms.service.impl;

import com.uti.svc_rooms.dto.AvailabilityResponse;
import com.uti.svc_rooms.dto.RoomRequest;
import com.uti.svc_rooms.dto.RoomResponse;
import com.uti.svc_rooms.exception.BusinessRulesException;
import com.uti.svc_rooms.exception.DuplicateResourceException;
import com.uti.svc_rooms.exception.ResourceNotfoundException;
import com.uti.svc_rooms.mapper.RoomMapper;
import com.uti.svc_rooms.model.Room;
import com.uti.svc_rooms.repository.RoomRepository;
import com.uti.svc_rooms.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        log.info("Obteniendo todas las habitaciones");
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        log.info("Obteniendo habitación con id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException(
                        "Habitación no encontrada con el id: " + id));
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        log.info("Creando nueva habitación con número: {}", request.getRoomNumber());

        // Validar que el roomNumber no exista
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            log.warn("Intento de crear habitación con roomNumber duplicado: {}", request.getRoomNumber());
            throw new DuplicateResourceException(
                    "Ya existe una habitación con el número: " + request.getRoomNumber());
        }

        // Validar que availableRooms no sea negativo
        if (request.getAvailableRooms() < 0) {
            log.warn("Intento de crear habitación con availableRooms negativo");
            throw new BusinessRulesException(
                    "Las habitaciones disponibles no pueden ser negativas");
        }

        // Validar que availableRooms no supere totalCapacity
        if (request.getAvailableRooms() > request.getTotalCapacity()) {
            log.warn("availableRooms ({}) supera totalCapacity ({})",
                    request.getAvailableRooms(), request.getTotalCapacity());
            throw new BusinessRulesException(
                    "Las habitaciones disponibles (" + request.getAvailableRooms() +
                            ") no pueden exceder el total (" + request.getTotalCapacity() + ")");
        }

        Room room = roomMapper.toEntity(request);
        Room savedRoom = roomRepository.save(room);

        log.info("Habitación creada exitosamente con id: {}", savedRoom.getId());
        return roomMapper.toResponse(savedRoom);
    }


    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        log.info("Actualizando habitación con id: {}", id);

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException(
                        "Habitación no encontrada con id: " + id));

        // Validar que el roomNumber no exista en otra habitación
        if (roomRepository.existsByRoomNumberAndIdNot(request.getRoomNumber(), id)) {
            log.warn("Intento de actualizar habitación con roomNumber duplicado: {}", request.getRoomNumber());
            throw new DuplicateResourceException(
                    "Ya existe otra habitación con el número: " + request.getRoomNumber());
        }

        // Validar que availableRooms no sea negativo
        if (request.getAvailableRooms() < 0) {
            log.warn("Intento de actualizar habitación con availableRooms negativo");
            throw new BusinessRulesException(
                    "Las habitaciones disponibles no pueden ser negativas");
        }

        // Validar que availableRooms no supere totalCapacity
        if (request.getAvailableRooms() > request.getTotalCapacity()) {
            log.warn("availableRooms ({}) supera totalCapacity ({})",
                    request.getAvailableRooms(), request.getTotalCapacity());
            throw new BusinessRulesException(
                    "Las habitaciones disponibles (" + request.getAvailableRooms() +
                            ") no pueden exceder el total (" + request.getTotalCapacity() + ")");
        }

        roomMapper.updateEntityFromRequest(request, existingRoom);
        Room updatedRoom = roomRepository.save(existingRoom);

        log.info("Habitación actualizada exitosamente con id: {}", updatedRoom.getId());
        return roomMapper.toResponse(updatedRoom);
    }


    @Override
    @Transactional
    public RoomResponse updateAvailability(Long id, Integer availableRooms) {
        log.info("Actualizando disponibilidad de habitación id: {} con {} habitaciones disponibles",
                id, availableRooms);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException(
                        "Habitación no encontrada con id: " + id));

        // Validar que availableRooms no sea negativo
        if (availableRooms < 0) {
            log.warn("Intento de actualizar availableRooms con valor negativo: {}", availableRooms);
            throw new BusinessRulesException(
                    "Las habitaciones disponibles no pueden ser negativas");
        }

        // Validar que availableRooms no supere totalCapacity
        if (availableRooms > room.getTotalCapacity()) {
            log.warn("availableRooms ({}) supera totalCapacity ({})",
                    availableRooms, room.getTotalCapacity());
            throw new BusinessRulesException(
                    "Las habitaciones disponibles (" + availableRooms +
                            ") no pueden exceder el total (" + room.getTotalCapacity() + ")");
        }

        room.setAvailableRooms(availableRooms);
        Room updatedRoom = roomRepository.save(room);

        log.info("Disponibilidad actualizada exitosamente");
        return roomMapper.toResponse(updatedRoom);
    }


    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(Long id) {
        log.info("Verificando disponibilidad de habitación id: {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException(
                        "Habitación no encontrada con id: " + id));

        boolean available = room.getAvailableRooms() > 0;

        log.info("Disponibilidad verificada - Disponible: {}, Cantidad: {}",
                available, room.getAvailableRooms());

        return AvailabilityResponse.builder()
                .roomId(room.getId())
                .available(available)
                .availableRooms(room.getAvailableRooms())
                .build();
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        log.info("Eliminando habitación con id: {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException(
                        "Habitación no encontrada con id: " + id));

        roomRepository.delete(room);
        log.info("Habitación eliminada exitosamente");
    }
}