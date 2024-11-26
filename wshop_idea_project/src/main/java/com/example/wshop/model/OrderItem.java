package com.example.wshop.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "\"OrderItem\"")
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderid")
    @JoinColumn(name = "orderid", nullable = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productid")
    @JoinColumn(name = "productid", nullable = true)
    private Product product;

    @Column(nullable = false)
    private int itemcount;
}
