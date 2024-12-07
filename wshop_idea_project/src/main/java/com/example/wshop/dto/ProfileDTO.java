package com.example.wshop.dto;

import com.example.wshop.anatation.ValidDate;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {
    private Long profileid;

    @Size(max = 50, message = "Firstname must not exceed 50 characters")
    private String firstname;

    @Size(max = 50, message = "Lastname must not exceed 50 characters")
    private String lastname;

    @ValidDate
    private String birthday;

    @Pattern(regexp = "Male|Female", message = "Gender must be Male or Female")
    private String gender;
}
