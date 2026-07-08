package org.bms.movieticketbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bms.movieticketbooking.dto.request.ConfirmBookingRequest;
import org.bms.movieticketbooking.dto.request.HoldSeatsRequest;
import org.bms.movieticketbooking.dto.response.ApiResponse;
import org.bms.movieticketbooking.dto.response.BookingResponse;
import org.bms.movieticketbooking.entity.supporting.User;
import org.bms.movieticketbooking.repository.UserRepository;
import org.bms.movieticketbooking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<String>> holdSeats(
            @Valid @RequestBody HoldSeatsRequest request, Authentication auth) {
        Long userId = getUserId(auth);
        String result = bookingService.holdSeats(userId, request.getShowId(), request.getShowSeatIds());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(
            @Valid @RequestBody ConfirmBookingRequest request, Authentication auth) {
        Long userId = getUserId(auth);
        BookingResponse response = bookingService.confirmBooking(
                userId, request.getShowId(), request.getShowSeatIds(),
                request.getPaymentMethod(), request.getDiscountCode());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long bookingId, Authentication auth) {
        Long userId = getUserId(auth);
        BookingResponse response = bookingService.cancelBooking(userId, bookingId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUserBookings(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(ApiResponse.success(bookingService.getUserBookings(userId)));
    }

    private Long getUserId(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
