package com.example.wshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    @NotNull(message = "Order id cannot be null")
    private Long orderid;

    @NotNull(message = "Product id cannot be null")
    private Long productid;

    private String productname;

    @NotNull(message = "Item count cannot be null")
    @Positive(message = "Item count must be positive")
    private Integer itemcount;
}
