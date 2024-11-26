package com.example.wshop.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "\"Role\"")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleid;

    @Column(nullable = false, unique = true,length = 10)
    private String rolename;
}
