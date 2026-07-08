package org.bms.movieticketbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bms.movieticketbooking.dto.request.*;
import org.bms.movieticketbooking.enums.PricingTier;
import org.bms.movieticketbooking.enums.SeatType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullBookingFlow() throws Exception {
        String adminEmail = "admin-" + UUID.randomUUID() + "@test.com";
        String customerEmail = "customer-" + UUID.randomUUID() + "@test.com";

        // Register admin (will be CUSTOMER role via API, but we use the seeded admin)
        // Use the default admin credentials from DataInitializer
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@moviebooking.com");
        adminLogin.setPassword("admin123");

        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String adminToken = objectMapper.readTree(adminResult.getResponse().getContentAsString())
                .get("data").get("token").asText();

        // Register customer
        RegisterRequest customerReg = new RegisterRequest();
        customerReg.setEmail(customerEmail);
        customerReg.setPassword("password123");
        customerReg.setName("Customer");

        MvcResult customerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerReg)))
                .andExpect(status().isOk())
                .andReturn();

        String customerToken = objectMapper.readTree(customerResult.getResponse().getContentAsString())
                .get("data").get("token").asText();

        // Admin creates city
        CreateCityRequest cityReq = new CreateCityRequest();
        cityReq.setName("City-" + UUID.randomUUID());

        MvcResult cityResult = mockMvc.perform(post("/api/admin/cities")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityReq)))
                .andExpect(status().isOk())
                .andReturn();

        Long cityId = objectMapper.readTree(cityResult.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // Admin creates theatre
        CreateTheatreRequest theatreReq = new CreateTheatreRequest();
        theatreReq.setName("Theatre-" + UUID.randomUUID());
        theatreReq.setAddress("123 Main St");
        theatreReq.setCityId(cityId);

        MvcResult theatreResult = mockMvc.perform(post("/api/admin/theatres")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreReq)))
                .andExpect(status().isOk())
                .andReturn();

        Long theatreId = objectMapper.readTree(theatreResult.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // Admin creates screen with seats
        CreateScreenRequest screenReq = new CreateScreenRequest();
        screenReq.setName("Screen 1");
        screenReq.setTheatreId(theatreId);
        CreateScreenRequest.SeatRow row = new CreateScreenRequest.SeatRow();
        row.setRowLabel("A");
        row.setSeatCount(5);
        row.setSeatType(SeatType.REGULAR);
        screenReq.setSeatLayout(List.of(row));

        MvcResult screenResult = mockMvc.perform(post("/api/admin/screens")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenReq)))
                .andExpect(status().isOk())
                .andReturn();

        Long screenId = objectMapper.readTree(screenResult.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // Admin creates movie
        CreateMovieRequest movieReq = new CreateMovieRequest();
        movieReq.setTitle("Test Movie");
        movieReq.setDescription("A test movie");
        movieReq.setDurationMinutes(120);
        movieReq.setLanguage("English");
        movieReq.setGenre("Action");

        MvcResult movieResult = mockMvc.perform(post("/api/admin/movies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieReq)))
                .andExpect(status().isOk())
                .andReturn();

        Long movieId = objectMapper.readTree(movieResult.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // Admin creates show
        CreateShowRequest showReq = new CreateShowRequest();
        showReq.setMovieId(movieId);
        showReq.setScreenId(screenId);
        showReq.setStartTime(LocalDateTime.now().plusDays(1));
        showReq.setPricingTier(PricingTier.STANDARD);

        MvcResult showResult = mockMvc.perform(post("/api/admin/shows")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showReq)))
                .andExpect(status().isOk())
                .andReturn();

        Long showId = objectMapper.readTree(showResult.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // Admin creates pricing rule
        CreatePricingRuleRequest pricingReq = new CreatePricingRuleRequest();
        pricingReq.setSeatType(SeatType.REGULAR);
        pricingReq.setPricingTier(PricingTier.STANDARD);
        pricingReq.setPrice(BigDecimal.valueOf(250));

        mockMvc.perform(post("/api/admin/pricing-rules")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pricingReq)))
                .andExpect(status().isOk());

        // Customer browses seats
        MvcResult seatsResult = mockMvc.perform(get("/api/shows/" + showId + "/seats")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andReturn();

        Long showSeatId = objectMapper.readTree(seatsResult.getResponse().getContentAsString())
                .get("data").get(0).get("showSeatId").asLong();

        // Customer holds seats
        HoldSeatsRequest holdReq = new HoldSeatsRequest();
        holdReq.setShowId(showId);
        holdReq.setShowSeatIds(List.of(showSeatId));

        mockMvc.perform(post("/api/bookings/hold")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(holdReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Customer confirms booking
        ConfirmBookingRequest confirmReq = new ConfirmBookingRequest();
        confirmReq.setShowId(showId);
        confirmReq.setShowSeatIds(List.of(showSeatId));
        confirmReq.setPaymentMethod("CARD");

        mockMvc.perform(post("/api/bookings/confirm")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.totalAmount").value(250));
    }
}
