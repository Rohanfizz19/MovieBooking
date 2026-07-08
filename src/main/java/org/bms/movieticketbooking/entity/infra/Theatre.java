package org.bms.movieticketbooking.entity.infra;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "theatres")
@Data
public class Theatre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
}
