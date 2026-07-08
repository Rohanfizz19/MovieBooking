package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bms.movieticketbooking.enums.PricingTier;

import java.time.LocalDateTime;

@Data
public class CreateShowRequest {
    @NotNull
    private Long movieId;
    @NotNull
    private Long screenId;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private PricingTier pricingTier;
}
