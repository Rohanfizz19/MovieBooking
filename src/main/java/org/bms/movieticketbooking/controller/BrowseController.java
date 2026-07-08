package org.bms.movieticketbooking.controller;

import lombok.RequiredArgsConstructor;
import org.bms.movieticketbooking.dto.response.ApiResponse;
import org.bms.movieticketbooking.entity.content.Movie;
import org.bms.movieticketbooking.entity.content.Show;
import org.bms.movieticketbooking.entity.infra.City;
import org.bms.movieticketbooking.service.BrowseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BrowseController {

    private final BrowseService browseService;

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<City>>> getCities() {
        return ResponseEntity.ok(ApiResponse.success(browseService.getCities()));
    }

    @GetMapping("/cities/{cityId}/movies")
    public ResponseEntity<ApiResponse<List<Movie>>> getMoviesByCity(@PathVariable Long cityId) {
        return ResponseEntity.ok(ApiResponse.success(browseService.getMoviesByCity(cityId)));
    }

    @GetMapping("/movies/{movieId}/shows")
    public ResponseEntity<ApiResponse<List<Show>>> getShowsForMovie(
            @PathVariable Long movieId, @RequestParam Long cityId) {
        return ResponseEntity.ok(ApiResponse.success(browseService.getShowsForMovie(movieId, cityId)));
    }

    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSeatsForShow(@PathVariable Long showId) {
        return ResponseEntity.ok(ApiResponse.success(browseService.getSeatsForShow(showId)));
    }
}
