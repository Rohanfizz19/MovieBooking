package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ConfirmBookingRequest {
    @NotNull
    private Long showId;
    @NotNull
    private List<Long> showSeatIds;
    @NotBlank
    private String paymentMethod;
    private String discountCode;
}
