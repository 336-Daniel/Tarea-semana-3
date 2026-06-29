package com.uti.svcreservations.dto;

import com.uti.svcreservations.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.engine.spi.ManagedEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {


    @NotNull(message = "ID del cuarto es requerido")
    private Long roomId;

    @NotBlank(message = "El nombre del huesped es requerido")
    @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres")
    private String guestName;

    @NotBlank(message = "El email es requerido")
    @Size(max = 200, message = "El email no puede exceder los 200 caracteres")
    private String guestEmail;

    @NotNull(message = "La fecha de entrada es requerida")
    private LocalDate checkInDate;

    @NotNull(message = "La fecha de salida es requerida")
    private LocalDate checkOutDate;

    @NotNull(message = "El estatus es requerido")
    private Status status ;

    @NotNull(message = "El total de noches es requerido")
    private Integer totalNights;

    @NotNull(message = "la fecha de creacion es requerida")
    private LocalDateTime createdAt;

}
