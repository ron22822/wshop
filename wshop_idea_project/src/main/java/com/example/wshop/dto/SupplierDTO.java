package com.example.wshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierDTO {
    private Long supplierid;

    @NotBlank(message = "Supplier name cannot be blank")
    @Size(max = 250, message = "Supplier name must not exceed 250 characters")
    private String suppliername;

    @Size(max = 1000, message = "Contact info must not exceed 1000 characters")
    private String contactinfo;
}
