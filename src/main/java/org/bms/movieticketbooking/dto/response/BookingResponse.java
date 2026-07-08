package org.bms.movieticketbooking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {
    private Long bookingId;
    private String movieTitle;
    private String theaterName;
    private String screenName;
    private LocalDateTime showTime;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountApplied;
    private LocalDateTime createdAt;
    private List<String> seats;
}
