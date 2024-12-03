package com.example.wshop.service;

import com.example.wshop.constant.ActivityEnum;
import com.example.wshop.constant.StatusEnum;
import com.example.wshop.dto.OrderItemDTO;
import com.example.wshop.exception.InvalidRequestDataException;
import com.example.wshop.exception.ResourceNotFoundException;
import com.example.wshop.model.*;
import com.example.wshop.repository.OrderItemRepository;
import com.example.wshop.repository.OrderRepository;
import com.example.wshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, ProductRepository productRepository
            , OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public List<OrderItemDTO> getAllForOrder(Long orderid){
        Order order = orderRepository.findById(orderid)
                .orElseThrow(()  -> new ResourceNotFoundException("Order not found with Id: " + orderid));
        return orderItemRepository.findAllForOrder(orderid)
                .stream().map(this::mapToDto).toList();
    }

    public List<OrderItemDTO> getAllForOrder(User user,Long orderid){
        Order order = orderRepository.findById(orderid)
                .orElseThrow(()  -> new ResourceNotFoundException("Order not found with Id: " + orderid));
        if(!order.getUser().getUserid().equals(user.getUserid())){
            throw new AccessDeniedException("Not have access rights to this user's order");
        }
        return orderItemRepository.findAllForOrder(orderid)
                .stream().map(this::mapToDto).toList();
    }

    public Page<OrderItemDTO> getAllOrderItem(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderItem> orderItems = orderItemRepository.findAll(pageable);
        return orderItems.map(this::mapToDto);
    }

    @Transactional
    public OrderItemDTO createOrderItem(User user, OrderItemDTO orderItemDTO){
        Product product = productRepository.findById(orderItemDTO.getProductid()).
                orElseThrow(()  -> new ResourceNotFoundException("Product not found with Id: " + orderItemDTO.getProductid()));
        Order order = orderRepository.findById(orderItemDTO.getOrderid())
                .orElseThrow(()  -> new ResourceNotFoundException("Order not found with Id: " + orderItemDTO.getOrderid()));
        OrderItemId orderItemId = new OrderItemId();
        orderItemId.setProductid(product.getProductid());
        orderItemId.setOrderid(order.getOrderid());

        checkAccessRights(order,user);
        if(!checkOrderStatusIsCreated(order)){
            throw new InvalidRequestDataException("Order is completed or cancelled");
        }
        if(ActivityEnum.INACTIVE.getString().equals(product.getActivity())){
            throw new InvalidRequestDataException("Product is inactive");
        }
        if(orderItemDTO.getItemcount() > product.getTotalquantity()){
            throw new InvalidRequestDataException("There is not enough product "+product.getProductname()+" available");
        }
        if(orderItemRepository.findById(orderItemId).isPresent()){
            deleteOrderItem(orderItemId);
        }

        product.setTotalquantity(product.getTotalquantity()-orderItemDTO.getItemcount());
        productRepository.save(product);
        order.setPositioncount(order.getPositioncount()+1);
        BigDecimal updateTotalPrice = order.getTotalprice();
        updateTotalPrice = updateTotalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(orderItemDTO.getItemcount())));
        order.setTotalprice(updateTotalPrice);
        orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        orderItem.setItemcount(orderItemDTO.getItemcount());
        orderItem.setOrderid(order.getOrderid());
        orderItem.setProductid(product.getProductid());
        orderItem = orderItemRepository.save(orderItem);

        return mapToDto(orderItem);
    }

    @Transactional
    public void deleteOrderItem(User user,OrderItemId orderItemId){
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(()  -> new ResourceNotFoundException("Order Item not found with oder id: "
                        +orderItemId.getOrderid()+" product id: "
                        +orderItemId.getProductid()));
        Order order = orderRepository.findById(orderItem.getOrderid()).get();
        checkAccessRights(order,user);
        if(!checkOrderStatusIsCreated(order)){
            throw new InvalidRequestDataException("Order is completed or cancelled");
        }
        Product product = productRepository.findById(orderItem.getProductid()).get();
        processDeleteOrderItem(orderItem,order,product);
    }

    @Transactional
    public void deleteOrderItem(OrderItemId orderItemId){
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(()  -> new ResourceNotFoundException("Order Item not found with oder id: "
                        +orderItemId.getOrderid()+" product id: "
                        +orderItemId.getProductid()));
        Order order = orderRepository.findById(orderItem.getOrderid()).get();
        if(!checkOrderStatusIsCreated(order)){
            throw new InvalidRequestDataException("Order is completed or cancelled");
        }
        Product product = productRepository.findById(orderItem.getProductid()).get();
        processDeleteOrderItem(orderItem,order,product);
    }

    @Transactional
    public void deleteAllOrderItemForProduct(Product product){
        List<OrderItem> orderItems = orderItemRepository.findAllForProduct(product.getProductid());
        for(OrderItem orderItem : orderItems){
            Order order = orderRepository.findById(orderItem.getOrderid()).get();
            if(!checkOrderStatusIsCreated(order)){
                return;
            }
            processDeleteOrderItem(orderItem,order,product);
        }
    }

    private void processDeleteOrderItem(OrderItem orderItem, Order order, Product product){
        product.setTotalquantity(product.getTotalquantity()+orderItem.getItemcount());
        productRepository.saveAndFlush(product);
        order.setPositioncount(order.getPositioncount()-1);
        BigDecimal retTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(orderItem.getItemcount()));
        order.setTotalprice(order.getTotalprice().subtract(retTotalPrice));
        orderRepository.saveAndFlush(order);
        orderItemRepository.delete(orderItem);
    }

    private void checkAccessRights(Order order,User user){
        if(!order.getUser().getUserid().equals(user.getUserid())){
            throw new AccessDeniedException("Not have access rights to this user's order");
        }
    }

    private Boolean checkOrderStatusIsCreated(Order order){
        return StatusEnum.CREATED.getString().equals(order.getStatus());
    }

    @Transactional
    public void retTotalQuantity(OrderItem orderItem){
        Product product = productRepository.findById(orderItem.getProductid()).get();
        product.setTotalquantity(product.getTotalquantity()+orderItem.getItemcount());
        productRepository.saveAndFlush(product);
    }

    private OrderItemDTO mapToDto(OrderItem orderItem){
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderid(orderItem.getId().getOrderid());
        orderItemDTO.setProductid(orderItem.getId().getProductid());
        orderItemDTO.setItemcount(orderItem.getItemcount());
        Product product = productRepository.findById(orderItem.getProductid()).get();
        orderItemDTO.setProductname(product.getProductname());
        return orderItemDTO;
    }
}
