package com.uti.svcreservations.mapper;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequest request) {
        return Reservation.builder()
                .roomId(request.getRoomId())
                .guestName(request.getGuestName())
                .guestEmail(request.getGuestEmail())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .status(request.getStatus())
                .build();
    }

    public ReservationResponse toResponse (Reservation reservation) {
        return ReservationResponse.builder()
                .Id(reservation.getId())
                .roomId(reservation.getRoomId())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .status(reservation.getStatus())
                .totalNights(reservation.getTotalNights())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    public void updateEntityFromRequest(ReservationRequest request, Reservation reservation) {

        reservation.setRoomId(request.getRoomId());
        reservation.setGuestName(request.getGuestName());
        reservation.setGuestEmail(request.getGuestEmail());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setStatus(request.getStatus());


    }
}
