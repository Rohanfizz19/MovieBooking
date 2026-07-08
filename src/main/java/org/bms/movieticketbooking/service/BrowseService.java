package org.bms.movieticketbooking.service;

import lombok.RequiredArgsConstructor;
import org.bms.movieticketbooking.entity.content.Movie;
import org.bms.movieticketbooking.entity.content.Show;
import org.bms.movieticketbooking.entity.content.ShowSeat;
import org.bms.movieticketbooking.entity.infra.City;
import org.bms.movieticketbooking.entity.supporting.PricingRule;
import org.bms.movieticketbooking.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrowseService {

    private final CityRepository cityRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final PricingRuleRepository pricingRuleRepository;

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public List<Movie> getMoviesByCity(Long cityId) {
        return movieRepository.findMoviesByCityId(cityId);
    }

    public List<Show> getShowsForMovie(Long movieId, Long cityId) {
        return showRepository.findByMovieIdAndCityId(movieId, cityId);
    }

    public List<Map<String, Object>> getSeatsForShow(Long showId) {
        List<ShowSeat> showSeats = showSeatRepository.findByShowId(showId);
        return showSeats.stream().map(ss -> {
            Map<String, Object> map = new HashMap<>();
            map.put("showSeatId", ss.getId());
            map.put("rowLabel", ss.getSeat().getRowLabel());
            map.put("seatNumber", ss.getSeat().getSeatNumber());
            map.put("seatType", ss.getSeat().getSeatType().name());
            map.put("status", ss.getStatus().name());

            BigDecimal price = pricingRuleRepository
                    .findBySeatTypeAndPricingTier(ss.getSeat().getSeatType(), ss.getShow().getPricingTier())
                    .map(PricingRule::getPrice)
                    .orElse(BigDecimal.ZERO);
            map.put("price", price);
            return map;
        }).toList();
    }
}
