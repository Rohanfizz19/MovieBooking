package org.bms.movieticketbooking.service;

import lombok.RequiredArgsConstructor;
import org.bms.movieticketbooking.dto.request.*;
import org.bms.movieticketbooking.entity.content.Movie;
import org.bms.movieticketbooking.entity.content.Show;
import org.bms.movieticketbooking.entity.content.ShowSeat;
import org.bms.movieticketbooking.entity.infra.*;
import org.bms.movieticketbooking.entity.supporting.DiscountCode;
import org.bms.movieticketbooking.entity.supporting.PricingRule;
import org.bms.movieticketbooking.entity.supporting.RefundPolicy;
import org.bms.movieticketbooking.enums.SeatStatus;
import org.bms.movieticketbooking.exception.ResourceNotFoundException;
import org.bms.movieticketbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CityRepository cityRepository;
    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final RefundPolicyRepository refundPolicyRepository;

    public City createCity(CreateCityRequest request) {
        City city = new City();
        city.setName(request.getName());
        return cityRepository.save(city);
    }

    public Theatre createTheatre(CreateTheatreRequest request) {
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found"));
        Theatre theatre = new Theatre();
        theatre.setName(request.getName());
        theatre.setAddress(request.getAddress());
        theatre.setCity(city);
        return theatreRepository.save(theatre);
    }

    @Transactional
    public Screen createScreen(CreateScreenRequest request) {
        Theatre theatre = theatreRepository.findById(request.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));

        int totalSeats = request.getSeatLayout().stream()
                .mapToInt(CreateScreenRequest.SeatRow::getSeatCount).sum();

        Screen screen = new Screen();
        screen.setName(request.getName());
        screen.setTheater(theatre);
        screen.setTotalSeats(totalSeats);
        screen = screenRepository.save(screen);

        for (CreateScreenRequest.SeatRow row : request.getSeatLayout()) {
            for (int i = 1; i <= row.getSeatCount(); i++) {
                Seat seat = new Seat();
                seat.setScreen(screen);
                seat.setRowLabel(row.getRowLabel());
                seat.setSeatNumber(i);
                seat.setSeatType(row.getSeatType());
                seatRepository.save(seat);
            }
        }
        return screen;
    }

    public Movie createMovie(CreateMovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setLanguage(request.getLanguage());
        movie.setGenre(request.getGenre());
        return movieRepository.save(movie);
    }

    @Transactional
    public Show createShow(CreateShowRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getStartTime().plusMinutes(movie.getDurationMinutes()));
        show.setPricingTier(request.getPricingTier());
        show = showRepository.save(show);

        List<Seat> seats = seatRepository.findByScreenId(screen.getId());
        for (Seat seat : seats) {
            ShowSeat showSeat = new ShowSeat();
            showSeat.setShow(show);
            showSeat.setSeat(seat);
            showSeat.setStatus(SeatStatus.AVAILABLE);
            showSeatRepository.save(showSeat);
        }
        return show;
    }

    public PricingRule createPricingRule(CreatePricingRuleRequest request) {
        PricingRule rule = new PricingRule();
        rule.setSeatType(request.getSeatType());
        rule.setPricingTier(request.getPricingTier());
        rule.setPrice(request.getPrice());
        return pricingRuleRepository.save(rule);
    }

    public DiscountCode createDiscountCode(CreateDiscountCodeRequest request) {
        DiscountCode code = new DiscountCode();
        code.setCode(request.getCode());
        code.setPercentage(request.getPercentage());
        code.setMaxUses(request.getMaxUses());
        code.setCurrentUses(0);
        code.setValidUntil(request.getValidUntil());
        return discountCodeRepository.save(code);
    }

    public RefundPolicy createRefundPolicy(CreateRefundPolicyRequest request) {
        RefundPolicy policy = new RefundPolicy();
        policy.setName(request.getName());
        policy.setHoursBeforeShow(request.getHoursBeforeShow());
        policy.setRefundPercentage(request.getRefundPercentage());
        return refundPolicyRepository.save(policy);
    }
}
