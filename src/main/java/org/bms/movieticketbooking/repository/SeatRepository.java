package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.infra.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreenId(Long screenId);
}
