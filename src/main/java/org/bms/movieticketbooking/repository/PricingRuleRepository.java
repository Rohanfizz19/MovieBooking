package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.supporting.PricingRule;
import org.bms.movieticketbooking.enums.PricingTier;
import org.bms.movieticketbooking.enums.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    Optional<PricingRule> findBySeatTypeAndPricingTier(SeatType seatType, PricingTier pricingTier);
}
