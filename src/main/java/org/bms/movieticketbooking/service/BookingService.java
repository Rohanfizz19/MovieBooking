package org.bms.movieticketbooking.service;

import lombok.RequiredArgsConstructor;
import org.bms.movieticketbooking.dto.response.BookingResponse;
import org.bms.movieticketbooking.entity.content.Show;
import org.bms.movieticketbooking.entity.content.ShowSeat;
import org.bms.movieticketbooking.entity.supporting.*;
import org.bms.movieticketbooking.entity.transactions.Booking;
import org.bms.movieticketbooking.entity.transactions.BookingItem;
import org.bms.movieticketbooking.enums.*;
import org.bms.movieticketbooking.exception.BookingException;
import org.bms.movieticketbooking.exception.ResourceNotFoundException;
import org.bms.movieticketbooking.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final PaymentRepository paymentRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private final NotificationService notificationService;

    @Value("${app.booking.hold-timeout-minutes}")
    private int holdTimeoutMinutes;

    @Transactional
    public String holdSeats(Long userId, Long showId, List<Long> showSeatIds) {
        List<ShowSeat> seats = showSeatRepository.findByIdsWithLock(showSeatIds);

        for (ShowSeat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new BookingException("One or more seats are not available");
            }
        }

        LocalDateTime heldUntil = LocalDateTime.now().plusMinutes(holdTimeoutMinutes);
        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.HELD);
            seat.setHeldByUserId(userId);
            seat.setHeldUntil(heldUntil);
            showSeatRepository.save(seat);
        }

        return "Seats held until " + heldUntil;
    }

    @Transactional
    public BookingResponse confirmBooking(Long userId, Long showId, List<Long> showSeatIds,
                                          String paymentMethod, String discountCodeStr) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ShowSeat> seats = showSeatRepository.findByIdsWithLock(showSeatIds);
        for (ShowSeat seat : seats) {
            if (seat.getStatus() != SeatStatus.HELD || !userId.equals(seat.getHeldByUserId())) {
                throw new BookingException("Seats are not held by this user");
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ShowSeat seat : seats) {
            BigDecimal price = pricingRuleRepository
                    .findBySeatTypeAndPricingTier(seat.getSeat().getSeatType(), show.getPricingTier())
                    .map(PricingRule::getPrice)
                    .orElse(BigDecimal.valueOf(200));
            totalAmount = totalAmount.add(price);
        }

        BigDecimal discountApplied = BigDecimal.ZERO;
        if (discountCodeStr != null && !discountCodeStr.isBlank()) {
            DiscountCode discount = discountCodeRepository.findByCode(discountCodeStr)
                    .orElseThrow(() -> new BookingException("Invalid discount code"));
            if (discount.getCurrentUses() >= discount.getMaxUses()) {
                throw new BookingException("Discount code usage limit reached");
            }
            if (discount.getValidUntil().isBefore(LocalDateTime.now())) {
                throw new BookingException("Discount code expired");
            }
            discountApplied = totalAmount.multiply(BigDecimal.valueOf(discount.getPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalAmount = totalAmount.subtract(discountApplied);
            discount.setCurrentUses(discount.getCurrentUses() + 1);
            discountCodeRepository.save(discount);
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(totalAmount);
        booking.setDiscountApplied(discountApplied);
        booking.setCreatedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setHeldUntil(null);
            showSeatRepository.save(seat);

            BookingItem item = new BookingItem();
            item.setBooking(booking);
            item.setShowSeat(seat);
            BigDecimal price = pricingRuleRepository
                    .findBySeatTypeAndPricingTier(seat.getSeat().getSeatType(), show.getPricingTier())
                    .map(PricingRule::getPrice)
                    .orElse(BigDecimal.valueOf(200));
            item.setPrice(price);
            booking.getItems().add(item);
        }
        booking = bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentMethod(paymentMethod);
        paymentRepository.save(payment);

        notificationService.sendBookingConfirmation(user, booking);
        notificationService.scheduleReminder(user, booking, show.getStartTime());

        return buildBookingResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingException("Booking does not belong to this user");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }

        long hoursUntilShow = ChronoUnit.HOURS.between(LocalDateTime.now(), booking.getShow().getStartTime());
        int refundPercentage = 0;
        List<RefundPolicy> policies = refundPolicyRepository.findAllByOrderByHoursBeforeShowDesc();
        for (RefundPolicy policy : policies) {
            if (hoursUntilShow >= policy.getHoursBeforeShow()) {
                refundPercentage = policy.getRefundPercentage();
                break;
            }
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        for (BookingItem item : booking.getItems()) {
            ShowSeat showSeat = item.getShowSeat();
            showSeat.setStatus(SeatStatus.AVAILABLE);
            showSeat.setHeldByUserId(null);
            showSeatRepository.save(showSeat);
        }

        if (refundPercentage > 0) {
            Payment originalPayment = paymentRepository.findByBookingId(bookingId).orElse(null);
            if (originalPayment != null) {
                BigDecimal refundAmount = originalPayment.getAmount()
                        .multiply(BigDecimal.valueOf(refundPercentage))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                Payment refund = new Payment();
                refund.setBooking(booking);
                refund.setAmount(refundAmount);
                refund.setStatus(PaymentStatus.REFUNDED);
                refund.setPaymentMethod(originalPayment.getPaymentMethod());
                paymentRepository.save(refund);
            }
        }

        return buildBookingResponse(booking);
    }

    public List<BookingResponse> getUserBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return bookings.stream().map(this::buildBookingResponse).toList();
    }

    private BookingResponse buildBookingResponse(Booking booking) {
        Show show = booking.getShow();
        List<String> seatLabels = booking.getItems().stream()
                .map(item -> item.getShowSeat().getSeat().getRowLabel() + item.getShowSeat().getSeat().getSeatNumber())
                .toList();

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .movieTitle(show.getMovie().getTitle())
                .theaterName(show.getScreen().getTheater().getName())
                .screenName(show.getScreen().getName())
                .showTime(show.getStartTime())
                .status(booking.getStatus().name())
                .totalAmount(booking.getTotalAmount())
                .discountApplied(booking.getDiscountApplied())
                .createdAt(booking.getCreatedAt())
                .seats(seatLabels)
                .build();
    }
}
