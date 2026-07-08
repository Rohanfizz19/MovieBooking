package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.content.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT DISTINCT m FROM Movie m JOIN Show s ON s.movie = m " +
            "JOIN s.screen sc JOIN sc.theater t WHERE t.city.id = :cityId")
    List<Movie> findMoviesByCityId(@Param("cityId") Long cityId);
}
