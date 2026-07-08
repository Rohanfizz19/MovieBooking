package org.bms.movieticketbooking.entity.infra;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "screens")
@Data
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theater;

    @Column(nullable = false)
    private Integer totalSeats;
}
