package com.example.wshop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "\"Supplier\"")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierid;

    @Column(nullable = false, unique = true, length = 255)
    private String suppliername;

    @Column(length = 1000)
    private String contactinfo;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL,
            orphanRemoval = true,fetch = FetchType.LAZY )
    private Set<Product> products = new HashSet<>();
}

