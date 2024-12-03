package com.example.wshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Long orderid;
    private Long productid;
    private String productname;
    private Integer itemcount;
}
