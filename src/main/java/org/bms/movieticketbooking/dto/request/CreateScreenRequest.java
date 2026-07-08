package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bms.movieticketbooking.enums.SeatType;

import java.util.List;

@Data
public class CreateScreenRequest {
    @NotBlank
    private String name;
    @NotNull
    private Long theatreId;
    @NotNull
    private List<SeatRow> seatLayout;

    @Data
    public static class SeatRow {
        private String rowLabel;
        private Integer seatCount;
        private SeatType seatType;
    }
}
