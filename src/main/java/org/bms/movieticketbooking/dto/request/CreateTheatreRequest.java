package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTheatreRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotNull
    private Long cityId;
}
