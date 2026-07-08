package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.infra.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
}
