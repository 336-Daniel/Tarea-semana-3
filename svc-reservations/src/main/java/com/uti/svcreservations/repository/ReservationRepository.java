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
    List<Reservation> findAllByUserId(Long userId);
    List<Reservation> findAllByStatus(String status);
    List<Reservation> findAllByEmail(String email);


    String status(Status status);
}
