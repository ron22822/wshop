package com.example.wshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDTO {
    private Long orderid;
    private Long userid;
    private String orderdate;
    private String status;
    private BigDecimal totalprice;
    private int positioncount;
}
