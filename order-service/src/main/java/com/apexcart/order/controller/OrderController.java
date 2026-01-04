package com.apexcart.order.controller;

import com.apexcart.order.dto.OrderRequest;
import com.apexcart.order.dto.OrderResponse;
import com.apexcart.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Service", description = "Endpoints for managing customer orders")
public class OrderController
{

    private final OrderService orderService;
    @PostMapping
    @Operation(
            summary = "Place a new order",
            description = "Validates stock via Inventory Service and persists the order."
    )@ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "503", description = "Inventory service unavailable (Circuit Breaker)")
    public ResponseEntity<OrderResponse>plceOrder(@Valid @RequestBody OrderRequest orderRequest,
                                                  @AuthenticationPrincipal String username){
        OrderResponse response = orderService.createOrder(orderRequest,username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<OrderResponse>>getOrders(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse>getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
