package org.bms.movieticketbooking.entity.supporting;

import jakarta.persistence.*;
import lombok.Data;
import org.bms.movieticketbooking.entity.transactions.Booking;
import org.bms.movieticketbooking.enums.PaymentStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private String paymentMethod;
}
