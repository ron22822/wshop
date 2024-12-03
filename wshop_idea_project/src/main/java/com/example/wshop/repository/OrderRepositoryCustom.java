package com.example.wshop.repository;

import com.example.wshop.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Optional<Order> findOrderWithOrdersItem(Long orderid);

    List<Order> findAllUserOrders(Long userid);

}
