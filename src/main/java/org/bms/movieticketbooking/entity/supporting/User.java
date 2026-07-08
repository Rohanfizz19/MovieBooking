package org.bms.movieticketbooking.entity.supporting;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String password;

    @Column String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
