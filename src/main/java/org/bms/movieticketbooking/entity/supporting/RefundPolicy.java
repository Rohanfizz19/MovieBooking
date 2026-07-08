package org.bms.movieticketbooking.entity.supporting;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "refund_policies")
@Data
public class RefundPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer hoursBeforeShow;

    @Column(nullable = false)
    private Integer refundPercentage;
}
