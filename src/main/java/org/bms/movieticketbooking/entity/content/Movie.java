package org.bms.movieticketbooking.entity.content;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "movies")
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String genre;
}
