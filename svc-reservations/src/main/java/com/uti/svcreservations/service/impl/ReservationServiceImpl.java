package com.uti.svcreservations.service.impl;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.exception.BusinessRulesException;
import com.uti.svcreservations.exception.ResourceNotfoundException;
import com.uti.svcreservations.mapper.ReservationMapper;
import com.uti.svcreservations.model.Reservation;
import com.uti.svcreservations.model.Status;
import com.uti.svcreservations.repository.ReservationRepository;
import com.uti.svcreservations.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    //listar todas las reservas
    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations() {
        log.info("fetching all reservations");
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    //obtener reserva por id
    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        log.info("fetching reservation by id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotfoundException(
                        "Reservacion no encontrada con el id: " + id
                ));
        return reservationMapper.toResponse(reservation);
    }

    //obtner reserva por email
    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByEmail(String email) {
        log.info("fetching reservations by email: {}", email);
        return reservationRepository.findAllByGuestEmail(email)
                .stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    //crear reserva, (contiene algunas validaciones)
    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest Request) {
        log.info("creating reservation for guest: {}", Request.getGuestName());

        //validacion: checkOutDate debe ser posterior a checkInDate
        if(!Request.getCheckOutDate().isAfter(Request.getCheckInDate())) {
            throw new IllegalArgumentException(
                    "La fecha de salida debe ser posterior a la fecha de entrada");
        }

        //validacion: Un huésped no puede tener dos reservas ACTIVE para la misma habitación
        if (reservationRepository.existsByGuestEmailAndRoomIdAndStatus(Request.getGuestEmail(), Request.getRoomId(), Status.ACTIVE)) {
            throw new BusinessRulesException(
                    "El huésped ya tiene una reserva ACTIVA para esta habitación");
        }

        Reservation reservation = reservationMapper.toEntity(Request);
        reservation.setStatus(Status.ACTIVE);
        //calculo del CreateAt
        reservation.setCreatedAt(LocalDateTime.now());

        //calculo de TotalNights
        long daysBetween = ChronoUnit.DAYS.between(Request.getCheckInDate(), Request.getCheckOutDate());
        reservation.setTotalNights((int) daysBetween);

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reserva creada exitosamente con el id: {}", savedReservation.getId());

        return  reservationMapper.toResponse(savedReservation);

    }

    //eliminar reservacion
    @Override
    @Transactional
    public void deleteReservation(Long id) {
        log.info("deleting reservation by id: {}", id);
        if(!reservationRepository.existsById(id)){
            throw new ResourceNotfoundException(
                    "Reservacion no encontrada con id: "+ id);
        }

        reservationRepository.deleteById(id);
        log.info("reserva eliminada exitosamente con el id: {}", id);

    }

    //checkout de una reserva
    @Override
    @Transactional
    public ReservationResponse checkout(Long id) {
        log.info("checking out reservation by id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResolutionException(
                        "Reservacion no encontrada con id: " + id
                ));

        //validacion: Solo se puede hacer checkout de una reserva con status ACTIVE
        if (reservation.getStatus() != Status.ACTIVE) {
            throw new BusinessRulesException("" +
                    "Solo se puede hacer checkout de reservas ACTIVAS");
        }

        reservation.setStatus(Status.COMPLETED);

        Reservation updateReservation = reservationRepository.save(reservation);

        log.info("Checkout realizado exitosamente. Reserva con id: {} marcada como COMPLETED", id);

        return reservationMapper.toResponse(updateReservation) ;
    }

    //mostrar reservas por su estatus
    @Override
    @Transactional
    public List<ReservationResponse> getReservationsByStatus(Status status) {
        log.info("fetching reservations by status: {}", status);
        return reservationRepository.findAllByStatus(status)
                .stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }
}
