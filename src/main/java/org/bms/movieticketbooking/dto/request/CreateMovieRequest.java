package org.bms.movieticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMovieRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private Integer durationMinutes;
    @NotBlank
    private String language;
    @NotBlank
    private String genre;
}
