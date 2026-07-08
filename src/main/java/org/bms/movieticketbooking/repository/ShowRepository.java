package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.content.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("SELECT s FROM Show s JOIN s.screen sc JOIN sc.theater t " +
            "WHERE s.movie.id = :movieId AND t.city.id = :cityId")
    List<Show> findByMovieIdAndCityId(@Param("movieId") Long movieId, @Param("cityId") Long cityId);
}
