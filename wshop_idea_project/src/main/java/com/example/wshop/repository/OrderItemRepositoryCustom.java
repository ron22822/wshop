package com.example.wshop.repository;

import com.example.wshop.model.OrderItem;

import java.util.List;

public interface OrderItemRepositoryCustom {

    List<OrderItem> findAllForOrder(Long orderid);

    List<OrderItem> findAllForProduct(Long productid);

}
