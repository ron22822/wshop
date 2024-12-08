package com.example.wshop.service;

import com.example.wshop.constant.StatusEnum;
import com.example.wshop.dto.OrderDTO;
import com.example.wshop.exception.InvalidRequestDataException;
import com.example.wshop.exception.ResourceNotFoundException;
import com.example.wshop.model.Order;
import com.example.wshop.model.OrderItem;
import com.example.wshop.model.User;
import com.example.wshop.repository.OrderItemRepository;
import com.example.wshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.sql.Date;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.orderItemRepository = orderItemRepository;
    }

    public OrderDTO getById(Long id){
        return orderRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(()  -> new ResourceNotFoundException("Order not found with Id: " + id));
    }

    public List<OrderDTO> getAllByUser(User user){
        return orderRepository.findAllUserOrders(user.getUserid())
                .stream().map(this::mapToDto).toList();
    }

    public Page<OrderDTO> getAllOrder(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::mapToDto);
    }

    @Transactional
    public OrderDTO setOrderStatusCompleted(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(()  -> new ResourceNotFoundException("Order not found with Id: " + id));
        if(!StatusEnum.CREATED.getString().equals(order.getStatus())){
            throw new InvalidRequestDataException("The order cannot be transferred to the completed status");
        }
        order.setStatus(StatusEnum.COMPLETED.getString());
        order = orderRepository.save(order);
        return mapToDto(order);
    }

    @Transactional
    public OrderDTO setOrderStatusCancelled(Long orderid, User user){
        Order order = orderRepository.findById(orderid)
                .orElseThrow(()  -> new ResourceNotFoundException("Order not found with Id: " + orderid));
        if(!order.getUser().getUserid().equals(user.getUserid())){
            throw new AccessDeniedException("Not have access rights to this user's order");
        }
        if(!StatusEnum.CREATED.getString().equals(order.getStatus())){
            throw new InvalidRequestDataException("This order cannot be transferred to the cancelled status");
        }
        List<OrderItem> orderItems = orderItemRepository.findAllForOrder(orderid);
        for(OrderItem orderItem : orderItems){
            orderItemService.retTotalQuantity(orderItem);
        }
        order.setStatus(StatusEnum.CANCELLED.getString());
        order = orderRepository.saveAndFlush(order);
        return mapToDto(order);
    }

    @Transactional
    public void deleteOrder(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(()  -> new ResourceNotFoundException("Order not found with Id: " + id));
        if(StatusEnum.CREATED.getString().equals(order.getStatus())){
            throw new InvalidRequestDataException("Сannot delete an order that is in the CREATED status");
        }
        orderRepository.delete(order);
    }

    @Transactional
    public OrderDTO createOrder(User user){
        Order order = new Order();
        order.setUser(user);
        order.setStatus(StatusEnum.CREATED.getString());
        order.setTotalprice(BigDecimal.valueOf(0));
        order.setPositioncount(0);
        LocalDate localDate = LocalDate.now();
        order.setOrderdate(Date.valueOf(localDate));
        order = orderRepository.save(order);
        return mapToDto(order);
    }

    private OrderDTO mapToDto(Order order){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderid(order.getOrderid());
        orderDTO.setUserid(order.getUser().getUserid());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalprice(order.getTotalprice());
        orderDTO.setPositioncount(order.getPositioncount());
        orderDTO.setOrderdate(order.getOrderdate().toString());
        return orderDTO;
    }

}
