package com.uti.svcreservations.service;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.model.Status;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest Request);

    List<ReservationResponse> getReservations();

    ReservationResponse getReservationById(Long id);

    List<ReservationResponse> getReservationsByEmail(String email);

    void deleteReservation(Long id);

    //(patch)
    ReservationResponse checkout(Long id);

    List<ReservationResponse> getReservationsByStatus(Status Status);


}
