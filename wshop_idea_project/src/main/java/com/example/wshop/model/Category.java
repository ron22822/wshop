package com.example.wshop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "\"Category\"")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryid;

    @Column(nullable = false, unique = true, length = 255)
    private String categoryname;

    @Lob
    private String info;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL,
            orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();
}
