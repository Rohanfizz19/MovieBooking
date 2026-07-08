package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.infra.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
