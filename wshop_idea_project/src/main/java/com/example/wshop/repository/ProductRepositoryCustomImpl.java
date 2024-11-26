package com.example.wshop.repository;

import com.example.wshop.constant.ActivityEnum;
import com.example.wshop.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Product> findActiveProduct() {
        String query = "SELECT p "+
                       "FROM Product p "+
                       "WHERE p.activity = :active";
        return entityManager.createQuery(query, Product.class)
                .setParameter("active", ActivityEnum.ACTIVE.getString())
                .getResultList();
    }
}
