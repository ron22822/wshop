package com.example.wshop.repository;

import com.example.wshop.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom{
    @PersistenceContext
    EntityManager entityManager;


    @Override
    public Optional<Order> findOrderWithOrdersItem(Long orderid) {
        String query = "SELECT o "+
                "FROM Order o "+
                "LEFT JOIN FETCH o.orderItems "+
                "WHERE o.orderid = :orderID";
        try {
            Order order = entityManager.createQuery(query, Order.class)
                    .setParameter("orderID", orderid)
                    .getSingleResult();
            return Optional.of(order);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findAllUserOrders(Long userid) {
        String query = "SELECT o "+
                "FROM Order o "+
                "LEFT JOIN FETCH o.orderItems "+
                "WHERE o.user.userid = :userID";
        return entityManager.createQuery(query, Order.class)
                .setParameter("userID", userid)
                .getResultList();
    }
}
