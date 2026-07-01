package com.uti.svcreservations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long Id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false, length = 200)
    private String guestName;

    @Column(nullable = false, length = 200)
    private String guestEmail;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private Status status ;

    @Column(nullable = false)
    private Integer totalNights;

    @Column(nullable = false)
    private LocalDateTime createdAt;



}
