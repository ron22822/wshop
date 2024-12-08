package com.example.wshop.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private Long productid;

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 250, message = "Product name must not exceed 250 characters")
    private String productname;

    @NotNull(message = "Price cannot be null")
    @Digits(integer = 10, fraction = 2, message = "Number must have at most 10 digits before and 2 after the decimal point")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Total quantity cannot be null")
    @PositiveOrZero(message = "Total quantity must be positive or zero")
    private Integer totalquantity;

    @NotBlank(message = "Activity name cannot be blank")
    private String activity;

    @NotBlank(message = "Category name cannot be blank")
    private String categoryname;

    @NotBlank(message = "Supplier name cannot be blank")
    private String suppliername;
}
