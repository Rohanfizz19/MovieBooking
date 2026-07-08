package org.bms.movieticketbooking.service;

import org.bms.movieticketbooking.dto.response.BookingResponse;
import org.bms.movieticketbooking.entity.content.Movie;
import org.bms.movieticketbooking.entity.content.Show;
import org.bms.movieticketbooking.entity.content.ShowSeat;
import org.bms.movieticketbooking.entity.infra.Screen;
import org.bms.movieticketbooking.entity.infra.Seat;
import org.bms.movieticketbooking.entity.infra.Theatre;
import org.bms.movieticketbooking.entity.supporting.*;
import org.bms.movieticketbooking.entity.transactions.Booking;
import org.bms.movieticketbooking.entity.transactions.BookingItem;
import org.bms.movieticketbooking.enums.*;
import org.bms.movieticketbooking.exception.BookingException;
import org.bms.movieticketbooking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private ShowRepository showRepository;
    @Mock private ShowSeatRepository showSeatRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private PricingRuleRepository pricingRuleRepository;
    @Mock private DiscountCodeRepository discountCodeRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private RefundPolicyRepository refundPolicyRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private BookingService bookingService;

    private Show show;
    private User user;
    private ShowSeat showSeat;
    private Seat seat;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingService, "holdTimeoutMinutes", 10);

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setDurationMinutes(120);

        Theatre theatre = new Theatre();
        theatre.setId(1L);
        theatre.setName("Test Theatre");

        Screen screen = new Screen();
        screen.setId(1L);
        screen.setName("Screen 1");
        screen.setTheater(theatre);

        show = new Show();
        show.setId(1L);
        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(LocalDateTime.now().plusDays(1));
        show.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        show.setPricingTier(PricingTier.STANDARD);

        user = User.builder().id(1L).email("test@example.com").name("Test").role(Role.CUSTOMER).password("pass").build();

        seat = new Seat();
        seat.setId(1L);
        seat.setRowLabel("A");
        seat.setSeatNumber(1);
        seat.setSeatType(SeatType.REGULAR);

        showSeat = new ShowSeat();
        showSeat.setId(1L);
        showSeat.setShow(show);
        showSeat.setSeat(seat);
        showSeat.setStatus(SeatStatus.AVAILABLE);
    }

    @Test
    void holdSeats_success() {
        when(showSeatRepository.findByIdsWithLock(List.of(1L))).thenReturn(List.of(showSeat));
        when(showSeatRepository.save(any())).thenReturn(showSeat);

        String result = bookingService.holdSeats(1L, 1L, List.of(1L));

        assertNotNull(result);
        assertTrue(result.startsWith("Seats held until"));
        assertEquals(SeatStatus.HELD, showSeat.getStatus());
        assertEquals(1L, showSeat.getHeldByUserId());
    }

    @Test
    void holdSeats_alreadyHeld_throws() {
        showSeat.setStatus(SeatStatus.HELD);
        when(showSeatRepository.findByIdsWithLock(List.of(1L))).thenReturn(List.of(showSeat));

        assertThrows(BookingException.class, () -> bookingService.holdSeats(1L, 1L, List.of(1L)));
    }

    @Test
    void confirmBooking_success() {
        showSeat.setStatus(SeatStatus.HELD);
        showSeat.setHeldByUserId(1L);

        PricingRule rule = new PricingRule();
        rule.setPrice(BigDecimal.valueOf(300));

        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(showSeatRepository.findByIdsWithLock(List.of(1L))).thenReturn(List.of(showSeat));
        when(pricingRuleRepository.findBySeatTypeAndPricingTier(SeatType.REGULAR, PricingTier.STANDARD))
                .thenReturn(Optional.of(rule));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });
        when(paymentRepository.save(any())).thenReturn(new Payment());

        BookingResponse response = bookingService.confirmBooking(1L, 1L, List.of(1L), "CARD", null);

        assertNotNull(response);
        assertEquals(BookingStatus.CONFIRMED.name(), response.getStatus());
        assertEquals(BigDecimal.valueOf(300), response.getTotalAmount());
        verify(notificationService).sendBookingConfirmation(eq(user), any(Booking.class));
    }

    @Test
    void confirmBooking_withDiscount() {
        showSeat.setStatus(SeatStatus.HELD);
        showSeat.setHeldByUserId(1L);

        PricingRule rule = new PricingRule();
        rule.setPrice(BigDecimal.valueOf(400));

        DiscountCode discount = new DiscountCode();
        discount.setCode("SAVE20");
        discount.setPercentage(20);
        discount.setMaxUses(10);
        discount.setCurrentUses(0);
        discount.setValidUntil(LocalDateTime.now().plusDays(7));

        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(showSeatRepository.findByIdsWithLock(List.of(1L))).thenReturn(List.of(showSeat));
        when(pricingRuleRepository.findBySeatTypeAndPricingTier(SeatType.REGULAR, PricingTier.STANDARD))
                .thenReturn(Optional.of(rule));
        when(discountCodeRepository.findByCode("SAVE20")).thenReturn(Optional.of(discount));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });
        when(paymentRepository.save(any())).thenReturn(new Payment());

        BookingResponse response = bookingService.confirmBooking(1L, 1L, List.of(1L), "CARD", "SAVE20");

        assertNotNull(response);
        assertEquals(new BigDecimal("320.00"), response.getTotalAmount());
        assertEquals(new BigDecimal("80.00"), response.getDiscountApplied());
    }

    @Test
    void cancelBooking_success() {
        BookingItem item = new BookingItem();
        item.setShowSeat(showSeat);
        item.setPrice(BigDecimal.valueOf(300));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setShow(show);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(BigDecimal.valueOf(300));
        booking.setDiscountApplied(BigDecimal.ZERO);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setItems(new ArrayList<>(List.of(item)));
        item.setBooking(booking);

        RefundPolicy policy = new RefundPolicy();
        policy.setHoursBeforeShow(12);
        policy.setRefundPercentage(75);

        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(300));
        payment.setPaymentMethod("CARD");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(refundPolicyRepository.findAllByOrderByHoursBeforeShowDesc()).thenReturn(List.of(policy));
        when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.of(payment));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(paymentRepository.save(any())).thenReturn(new Payment());

        BookingResponse response = bookingService.cancelBooking(1L, 1L);

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELLED.name(), response.getStatus());
        assertEquals(SeatStatus.AVAILABLE, showSeat.getStatus());
    }
}
