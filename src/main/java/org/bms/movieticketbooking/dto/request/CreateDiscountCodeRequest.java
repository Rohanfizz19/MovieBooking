package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateDiscountCodeRequest {
    @NotBlank
    private String code;
    @NotNull
    private Integer percentage;
    @NotNull
    private Integer maxUses;
    @NotNull
    private LocalDateTime validUntil;
}
