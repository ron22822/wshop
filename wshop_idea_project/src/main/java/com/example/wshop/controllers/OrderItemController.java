package com.example.wshop.controllers;

import com.example.wshop.constant.RoleEnum;
import com.example.wshop.dto.OrderDTO;
import com.example.wshop.dto.OrderItemDTO;
import com.example.wshop.model.OrderItemId;
import com.example.wshop.model.User;
import com.example.wshop.service.OrderItemService;
import com.example.wshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderitem")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final UserService userService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService, UserService userService) {
        this.orderItemService = orderItemService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<OrderItemDTO>> getAllOrderItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Page<OrderItemDTO> orderItems = orderItemService.getAllOrderItem(page, size);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItemsByOrderId(@PathVariable Long orderId){
        User user = userService.getCurrentUser();
        List<OrderItemDTO> orderItems;
        if(RoleEnum.ADMIN.getString().equals(user.getRole().getRolename())){
            orderItems = orderItemService.getAllForOrder(orderId);
        }
        else {
            orderItems = orderItemService.getAllForOrder(user,orderId);
        }
        return ResponseEntity.ok(orderItems);
    }

    @PostMapping
    public ResponseEntity<OrderItemDTO> createOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO){
        User user = userService.getCurrentUser();
        OrderItemDTO orderItem = orderItemService.createOrderItem(user,orderItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItem);
    }

    @PutMapping
    public ResponseEntity<OrderItemDTO> updateOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO){
        User user = userService.getCurrentUser();
        OrderItemDTO orderItem = orderItemService.createOrderItem(user,orderItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItem);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteOrderItem(@RequestBody OrderItemId orderItemId){
        User user = userService.getCurrentUser();
        if(RoleEnum.ADMIN.getString().equals(user.getRole().getRolename())){
            orderItemService.deleteOrderItem(orderItemId);
        }else {
            orderItemService.deleteOrderItem(user,orderItemId);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Order Item success delete oder id: "
                +orderItemId.getOrderid()+" product id: "
                +orderItemId.getProductid());
    }
}
