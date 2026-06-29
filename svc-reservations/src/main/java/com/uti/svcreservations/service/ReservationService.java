package com.uti.svcreservations.service;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.model.Status;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    List<ReservationResponse> getReservations();

    ReservationResponse getReservationById(Long id);

    ReservationResponse createReservation(ReservationRequest Request);

    ReservationResponse updateReservation(Long id, ReservationRequest Request);

    void deleteReservation(Long id);

    //(patch)
    ReservationResponse updateCheckOutDate(Long id, LocalDate checkOutDate);

    List<ReservationResponse> getReservationsByStatus(Status Status);


}
