package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.supporting.RefundPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Long> {
    List<RefundPolicy> findAllByOrderByHoursBeforeShowDesc();
}
