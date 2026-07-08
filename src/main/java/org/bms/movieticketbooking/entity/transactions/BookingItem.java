package org.bms.movieticketbooking.entity.transactions;

import jakarta.persistence.*;
import lombok.Data;
import org.bms.movieticketbooking.entity.content.ShowSeat;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_items")
@Data
public class BookingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_seat_id", nullable = false)
    private ShowSeat showSeat;

    @Column(nullable = false)
    private BigDecimal price;
}
