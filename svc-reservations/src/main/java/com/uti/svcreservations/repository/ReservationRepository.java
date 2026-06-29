package com.uti.svcreservations.repository;

import com.uti.svcreservations.model.Reservation;
import com.uti.svcreservations.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findById(Long id);
    List<Reservation> findAllByStatus(Status status);
    List<Reservation> findAllByGuestEmail(String guestEmail);



}
