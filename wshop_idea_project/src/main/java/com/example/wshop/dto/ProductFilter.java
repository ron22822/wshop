package com.example.wshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductFilter {
    private Long categoryId;
    private Long supplierId;
    private Integer minQuantity;
    private Integer maxQuantity;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
