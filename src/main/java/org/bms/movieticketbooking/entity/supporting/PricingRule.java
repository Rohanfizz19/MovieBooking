package org.bms.movieticketbooking.entity.supporting;

import jakarta.persistence.*;
import lombok.Data;
import org.bms.movieticketbooking.enums.PricingTier;
import org.bms.movieticketbooking.enums.SeatType;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_rules")
@Data
public class PricingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingTier pricingTier;

    @Column(nullable = false)
    private BigDecimal price;
}
