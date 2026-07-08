package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bms.movieticketbooking.enums.PricingTier;
import org.bms.movieticketbooking.enums.SeatType;

import java.math.BigDecimal;

@Data
public class CreatePricingRuleRequest {
    @NotNull
    private SeatType seatType;
    @NotNull
    private PricingTier pricingTier;
    @NotNull
    private BigDecimal price;
}
