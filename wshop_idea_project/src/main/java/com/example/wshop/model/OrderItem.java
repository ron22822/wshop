package com.example.wshop.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "\"OrderItem\"")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id;

    @Column(name = "orderid", nullable = false, insertable = false, updatable = false)
    private Long orderid;

    @Column(name = "productid", nullable = false, insertable = false, updatable = false)
    private Long productid;

    @Column(nullable = false)
    private int itemcount;
}
