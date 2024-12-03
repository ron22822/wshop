package com.example.wshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private Long productid;
    private String productname;
    private BigDecimal price;
    private Integer totalquantity;
    private String activity;
    private String categoryname;
    private String suppliername;
}
