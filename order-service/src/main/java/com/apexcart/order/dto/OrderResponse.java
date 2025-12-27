package com.apexcart.order.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long orderId;
    private String product;
    private Double totalPrice;
    private String status;
    private LocalDateTime orderDate;
}
