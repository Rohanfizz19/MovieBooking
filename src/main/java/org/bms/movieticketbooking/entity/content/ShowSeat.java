package org.bms.movieticketbooking.entity.content;

import jakarta.persistence.*;
import lombok.Data;
import org.bms.movieticketbooking.entity.infra.Seat;
import org.bms.movieticketbooking.enums.SeatStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "show_seats")
@Data
public class ShowSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    private LocalDateTime heldUntil;

    private Long heldByUserId;
}
