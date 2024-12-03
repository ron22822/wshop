package com.example.wshop.controllers;

import com.example.wshop.dto.OrderDTO;
import com.example.wshop.model.User;
import com.example.wshop.service.OrderService;
import com.example.wshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(@Lazy OrderService orderService,@Lazy UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id){
        OrderDTO orderDTO = orderService.getById(id);
        return ResponseEntity.ok(orderDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Page<OrderDTO> orders = orderService.getAllOrder(page, size);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all/me")
    public ResponseEntity<List<OrderDTO>> getAllOrdersCurrentUser(){
        User user = userService.getCurrentUser();
        List<OrderDTO> orders = orderService.getAllByUser(user);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(){
        User user = userService.getCurrentUser();
        OrderDTO orderDTO = orderService.createOrder(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/completed/{id}")
    public ResponseEntity<OrderDTO> setOrderStatusCompleted(@PathVariable Long id){
        OrderDTO orderDTO = orderService.setOrderStatusCompleted(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/cancelled/{id}")
    public ResponseEntity<OrderDTO> setOrderStatusCancelled(@PathVariable Long id){
        User user = userService.getCurrentUser();
        OrderDTO orderDTO = orderService.setOrderStatusCancelled(id,user);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted Order id: " + id);
    }
}
