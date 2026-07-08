package com.uti.svcreservations.service.impl;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.dto.RoomAvailabilityResponse;
import com.uti.svcreservations.dto.RoomResponse;
import com.uti.svcreservations.exception.BusinessRulesException;
import com.uti.svcreservations.exception.ResourceNotfoundException;
import com.uti.svcreservations.exception.RoomsServiceException;
import com.uti.svcreservations.mapper.ReservationMapper;
import com.uti.svcreservations.model.Reservation;
import com.uti.svcreservations.model.Status;
import com.uti.svcreservations.repository.ReservationRepository;
import com.uti.svcreservations.service.ReservationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
                .map(reservation ->
                {
                    try {
                        RoomResponse room = RestTemplateClient.getBookById(loan.getBookId());
                        return loanMapper.toResponseWithBook(loan, book);
                    }catch (Exception ex) {
                        log.warn("No se logro obtener el detalle de la habitacion de la reserva con id: {}", loan.getId());
                        return loanMapper.toResponse(loan);

                    }
                })
                .collect(Collectors.toList());
    }


    //obtener reserva por id
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "roomService", fallbackMethod = "getReservationByIdFallback" )
    @Retry(name = "roomService")
    public ReservationResponse getReservationById(Long id) {
        log.info("fetching reservation by id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotfoundException(
                        "Reservacion no encontrada con el id: " + id
                ));
        RoomResponse roomDetails = RestTemplateClient.getBookById(loan.getBookId());
        return reservationMapper.toResponse(reservation);
    }

    //fallback de getReservationById
    public ReservationResponse getReservationByIdFallback(Long id, Throwable throwable) {
        log.warn("Circuit Breaker ABIERTO - El metodo activo Fallback. Razon: {} ",
                throwable.getMessage());
        if (throwable instanceof ResourceNotfoundException) {
            throw (ResourceNotfoundException) throwable;
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException(
                        "Reserva no encontrada con id: " + id
                ));
        ReservationResponse response = reservationMapper.toResponse(reservation);
        response.setRoomNumber("Informacion de la habitacion temporalmente no disponible. ");
        response.setType("N/A");
        return response;
    }


    //obtner reserva por email
    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "roomService", fallbackMethod = "getReservationsByEmailFallback" )
    @Retry(name = "roomService")
    public List<ReservationResponse> getReservationsByEmail(String email) {
        log.info("fetching reservations by email: {}", email);
        return reservationRepository.findAllByGuestEmail(email)
                .stream()
                .map(reservation ->
                        {
                            try {
                                RoomResponse room = WebClient.getBookId(reservation.getRoomId());
                                return reservationMapper.toResponseWithRoom(reservation, room);
                            }catch (Exception ex) {
                                log.warn("No se logro obtener el detalle de la habitacion de la reserva con id: {}", reservation.getId());
                                return reservationMapper.toResponse(reservation);
                            }
                        }
                        )
                .collect(Collectors.toList());
    }



    //crear reserva, (contiene algunas validaciones)
    @Override
    @Transactional
    @CircuitBreaker(name = "roomService", fallbackMethod = "createReservationFallback")
    @Retry(name = "roomService")
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

        log.info("Verificando disponibilidad de la habitacion via RestTemplate....");
        RoomAvailabilityResponse availability = RestTemplateClient.get

        if (!availability.getAvailable()){
            throw new BusinessRulesException(
                    "Habitacion con id: "+ request.getBookId() + "No esta disponible "
                            + availability.getAvailableStock());
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

        log.info("Obteniendo detralles de la habitacion via WebClient....");
        BookResponse bookDetails = WebClient.getBookId(request.getBookId());

        return  reservationMapper.toResponse(savedReservation);

    }

    //fallback de createReservation
    public ReservationResponse createReservationFallback(ReservationRequest request, Throwable throwable) {
        log.warn("Circuit Breaker ABIERTO - El metodo activo Fallback. Razon: {} ",
                throwable.getMessage());
        if (throwable instanceof BusinessRulesException) {
            throw (BusinessRulesException) throwable;
        }

        throw new RoomsServiceException(
                "Temporalmente el servico de rooms no esta disponible. Porfavor intente mas tarde"
                        + "Razon: " + throwable.getMessage());
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
    @CircuitBreaker(name = "roomService", fallbackMethod = "checkoutFallback")
    @Retry(name = "roomService")
    public ReservationResponse checkout(Long id) {
        log.info("checking out reservation by id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new ResolutionException(
                        "Reservacion no encontrada con id: " + id
                ));

        //validacion: Solo se puede hacer checkout de una reserva con status ACTIVE
        if (reservation.getStatus() != Status.ACTIVE) {
            throw new BusinessRulesException(
                    "Solo se puede hacer checkout de reservas ACTIVAS");
        }

        reservation.setStatus(Status.COMPLETED);

        Reservation updateReservation = reservationRepository.save(reservation);

        log.info("Checkout realizado exitosamente. Reserva con id: {} marcada como COMPLETED", id);

        RoomResponse roomDetails = RestTemplateClient.getBookById(updateLoan.getBookId());
        return reservationMapper.toResponse(updateReservation) ;
    }

    //fallback de checkout
    public ReservationResponse checkoutFallback(Long id, Throwable throwable) {
        log.warn("Circuit Breaker ABIERTO - El metodo activo Fallback. Razon: {} ",
                throwable.getMessage());
        if (throwable instanceof ResourceNotfoundException ||
                throwable instanceof BusinessRulesException) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
        }


        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException(
                        "Reserva no encontrada con id: " + id
                ));
        ReservationResponse response = reservationMapper.toResponse(reservation);
        response.setRoomNumber("Informacion de la habitacion temporalmente no disponible. ");
        response.setType("N/A");
        return response;

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
