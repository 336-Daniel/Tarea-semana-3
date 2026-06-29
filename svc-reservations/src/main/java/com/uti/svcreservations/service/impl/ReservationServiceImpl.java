package com.uti.svcreservations.service.impl;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
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
import java.time.LocalDate;
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


    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations() {
        log.info("fetching all reservations");
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

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

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByEmail(String email) {
        log.info("fetching reservations by email: {}", email);
        return reservationRepository.findAllByGuestEmail(email)
                .stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest Request) {
        log.info("creating reservation for guest: {}", Request.getGuestName());
        Reservation reservation = reservationMapper.toEntity(Request);
        reservation.setStatus(Status.ACTIVE);
        reservation.setCreatedAt(LocalDateTime.now());

        long daysBetween = ChronoUnit.DAYS.between(Request.getCheckInDate(), Request.getCheckOutDate());
        reservation.setTotalNights((int) daysBetween);

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reserva creada exitosamente con el id: {}", savedReservation.getId());

        return  reservationMapper.toResponse(savedReservation);

    }


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

    @Override
    @Transactional
    public ReservationResponse checkout(Long id) {
        log.info("checking out reservation by id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResolutionException(
                        "Reservacion no encontrada con id: " + id
                ));
        reservation.setStatus(Status.COMPLETED);
        reservation.setCheckOutDate(LocalDate.now());

        Reservation updateReservation = reservationRepository.save(reservation);

        log.info("Checkout realizado exitosamente. Reserva con id: {} marcada como COMPLETED", id);

        return reservationMapper.toResponse(updateReservation) ;
    }


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
