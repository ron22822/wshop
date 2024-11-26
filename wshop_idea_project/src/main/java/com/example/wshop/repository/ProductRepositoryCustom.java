package com.example.wshop.repository;

import com.example.wshop.model.Product;

import java.util.List;

public interface ProductRepositoryCustom {

    public List<Product> findActiveProduct();

}
