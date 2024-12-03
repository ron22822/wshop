package com.example.wshop.repository;

import com.example.wshop.model.OrderItem;
import com.example.wshop.model.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId>,OrderItemRepositoryCustom {
}
