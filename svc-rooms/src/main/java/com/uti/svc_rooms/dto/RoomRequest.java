package com.uti.svc_rooms.dto;

import com.uti.svc_rooms.model.RoomType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    @NotBlank(message = "El número de habitación no puede estar vacío")
    @Size(max = 10, message = "El número de habitación no puede exceder 10 caracteres")
    private String roomNumber;

    @NotNull(message = "El tipo de habitación no puede ser nulo")
    private RoomType type;

    @NotNull(message = "El precio por noche no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double pricePerNight;

    @NotNull(message = "La capacidad total no puede ser nula")
    @Min(value = 1, message = "La capacidad total debe ser al menos 1")
    private Integer totalCapacity;

    @NotNull(message = "El número de habitaciones disponibles no puede ser nulo")
    @Min(value = 0, message = "Las habitaciones disponibles no pueden ser negativas")
    private Integer availableRooms;

    @Min(value = 1, message = "El piso debe ser al menos 1")
    private Integer floor;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
}
