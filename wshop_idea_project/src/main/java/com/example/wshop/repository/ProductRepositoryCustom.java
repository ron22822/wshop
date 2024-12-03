package com.example.wshop.repository;

import com.example.wshop.dto.ProductFilter;
import com.example.wshop.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryCustom {

    public List<Product> findAllActiveProductsWithCategoryAndSupplier();

    public Optional<Product> findProductWithCategoryAndSupplier(Long id);

    public List<Product> findAllProductsWithCategoryAndSupplier();

    List<Product> findByFilter(ProductFilter filter,Boolean active);
}
