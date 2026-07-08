package com.uti.svcreservations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomAvailabilityResponse {

    private Long roomId;

    private Boolean available;

    private Integer availableRooms;
}
