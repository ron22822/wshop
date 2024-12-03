package com.example.wshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class OrderItemId implements Serializable {

    @Column(name = "orderid")
    private Long orderid;

    @Column(name = "productid")
    private Long productid;

}

