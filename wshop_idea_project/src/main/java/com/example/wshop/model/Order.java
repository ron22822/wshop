package com.example.wshop.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "\"Order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    private Date orderdate;

    @Column(length = 10,nullable = false)
    private String status;

    private BigDecimal totalprice;
    private int positioncount;

    @OneToMany(mappedBy = "orderid", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();
}

