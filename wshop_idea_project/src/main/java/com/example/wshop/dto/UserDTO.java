package com.example.wshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long userid;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 5, max = 250, message = "Password cannot be less than 5 characters")
    private String password;

    @NotBlank(message = "Role cannot be blank")
    private String role;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Incorrect email format")
    private String email;
}
