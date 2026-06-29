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
    @Transactional
    public ReservationResponse createReservation(ReservationRequest Request) {
        Reservation reservation = reservationMapper.toEntity(Request);
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("nueva reserva creada con el id: {}", savedReservation.getId());
        return  reservationMapper.toResponse(savedReservation);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest Request) {
        log.info("updating reservation by id: {}", id);
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResolutionException(
                        "Reservacion no encontrada con el id: " + id
                ));

        reservationMapper.updateEntityFromRequest(Request,existingReservation);
        Reservation updateReservation = reservationRepository.save(existingReservation);
        log.info("reserva actualizada exitosamente con el id: {}", updateReservation.getId());
        return reservationMapper.toResponse(updateReservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        log.info("deleting reservation by id: {}", id);
        if(reservationRepository.existsById(id)){
            throw new ResourceNotfoundException(
                    "Reservacion no encontrada con id: "+ id);
        }

        reservationRepository.deleteById(id);
        log.info("reserva eliminada exitosamente con el id: {}", id);

    }

    @Override
    @Transactional
    public ReservationResponse updateCheckOutDate(Long id, LocalDate checkOutDate) {
        log.info("updating check out date by id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResolutionException(
                        "Reservacion no encontrada con id: " + id
                ));
        reservation.setCheckOutDate(checkOutDate);
        Reservation updatedReservation = reservationRepository.save(reservation);
        log.info("fecha de salida actualizada exitosamente con el id: {}", id);
        return reservationMapper.toResponse(updatedReservation);
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
