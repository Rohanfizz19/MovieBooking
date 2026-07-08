package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRefundPolicyRequest {
    @NotBlank
    private String name;
    @NotNull
    private Integer hoursBeforeShow;
    @NotNull
    private Integer refundPercentage;
}
