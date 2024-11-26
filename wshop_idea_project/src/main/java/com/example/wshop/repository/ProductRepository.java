package com.example.wshop.repository;

import com.example.wshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>,ProductRepositoryCustom{
}
