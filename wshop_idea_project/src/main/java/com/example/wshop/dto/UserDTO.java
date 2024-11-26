package com.example.wshop.dto;

import com.example.wshop.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long userid;
    private String username;
    private String password;
    private String role;
    private String email;
}
