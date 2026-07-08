package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.infra.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {
}
