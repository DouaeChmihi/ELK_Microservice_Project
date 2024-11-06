package com.example.orderservice.controller;


import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOlder(@RequestBody OrderRequest orderRequest){
        log.info("Received order request: {}", orderRequest);

        // Log the time before placing the order
        long startTime = System.currentTimeMillis();

        orderService.placeOrder(orderRequest);

        // Log the time after placing the order and calculate the execution time
        long endTime = System.currentTimeMillis();
        log.info("Order placed successfully in {} milliseconds", endTime - startTime);

        return "Order placed successfully";
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Long orderId) {
        log.info("Received request to delete order with id: {}", orderId);

        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (IllegalArgumentException e) {
            log.error("Error deleting order with id: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found with id: " + orderId);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        log.info("Received request to retrieve order with id: {}", orderId);

        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            log.error("Error retrieving order with id: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
