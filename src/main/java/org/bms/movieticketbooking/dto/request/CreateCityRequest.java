package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCityRequest {
    @NotBlank
    private String name;
}
