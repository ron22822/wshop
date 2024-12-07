package com.example.wshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long categoryid;

    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 250, message = "Category name must not exceed 250 characters")
    private String categoryname;

    @Size(max = 1000, message = "Info must not exceed 1000 characters")
    private String info;
}
