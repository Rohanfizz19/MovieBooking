package org.bms.movieticketbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bms.movieticketbooking.dto.request.*;
import org.bms.movieticketbooking.dto.response.ApiResponse;
import org.bms.movieticketbooking.entity.content.Movie;
import org.bms.movieticketbooking.entity.content.Show;
import org.bms.movieticketbooking.entity.infra.City;
import org.bms.movieticketbooking.entity.infra.Screen;
import org.bms.movieticketbooking.entity.infra.Theatre;
import org.bms.movieticketbooking.entity.supporting.DiscountCode;
import org.bms.movieticketbooking.entity.supporting.PricingRule;
import org.bms.movieticketbooking.entity.supporting.RefundPolicy;
import org.bms.movieticketbooking.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/cities")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCity(@Valid @RequestBody CreateCityRequest request) {
        City city = adminService.createCity(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", city.getId(), "name", city.getName())));
    }

    @PostMapping("/theatres")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTheatre(@Valid @RequestBody CreateTheatreRequest request) {
        Theatre theatre = adminService.createTheatre(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", theatre.getId(), "name", theatre.getName())));
    }

    @PostMapping("/screens")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createScreen(@Valid @RequestBody CreateScreenRequest request) {
        Screen screen = adminService.createScreen(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", screen.getId(), "name", screen.getName(), "totalSeats", screen.getTotalSeats())));
    }

    @PostMapping("/movies")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createMovie(@Valid @RequestBody CreateMovieRequest request) {
        Movie movie = adminService.createMovie(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", movie.getId(), "title", movie.getTitle())));
    }

    @PostMapping("/shows")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createShow(@Valid @RequestBody CreateShowRequest request) {
        Show show = adminService.createShow(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", show.getId(), "startTime", show.getStartTime().toString(), "endTime", show.getEndTime().toString())));
    }

    @PostMapping("/pricing-rules")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPricingRule(@Valid @RequestBody CreatePricingRuleRequest request) {
        PricingRule rule = adminService.createPricingRule(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", rule.getId(), "seatType", rule.getSeatType().name(), "pricingTier", rule.getPricingTier().name(), "price", rule.getPrice())));
    }

    @PostMapping("/discount-codes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createDiscountCode(@Valid @RequestBody CreateDiscountCodeRequest request) {
        DiscountCode code = adminService.createDiscountCode(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", code.getId(), "code", code.getCode())));
    }

    @PostMapping("/refund-policies")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createRefundPolicy(@Valid @RequestBody CreateRefundPolicyRequest request) {
        RefundPolicy policy = adminService.createRefundPolicy(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("id", policy.getId(), "name", policy.getName())));
    }
}
