package com.example.wshop.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class OrderItemId implements Serializable {
    private Long orderid;
    private Long productid;
}

