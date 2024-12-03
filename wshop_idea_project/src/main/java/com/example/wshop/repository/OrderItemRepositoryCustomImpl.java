package com.example.wshop.repository;

import com.example.wshop.model.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class OrderItemRepositoryCustomImpl implements OrderItemRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<OrderItem> findAllForOrder(Long orderid) {
        String query = "SELECT i "+
                    "FROM OrderItem i "+
                    "WHERE i.orderid = :orderID";
        return entityManager.createQuery(query, OrderItem.class)
                .setParameter("orderID", orderid)
                .getResultList();
    }

    @Override
    public List<OrderItem> findAllForProduct(Long productid) {
        String query = "SELECT i "+
                "FROM OrderItem i "+
                "WHERE i.productid = :productID";
        return entityManager.createQuery(query, OrderItem.class)
                .setParameter("productID", productid)
                .getResultList();
    }
}
