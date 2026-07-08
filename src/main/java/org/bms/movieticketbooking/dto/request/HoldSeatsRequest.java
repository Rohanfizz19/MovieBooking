package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class HoldSeatsRequest {
    @NotNull
    private Long showId;
    @NotNull
    private List<Long> showSeatIds;
}
