package com.apexcart.order.service;

import com.apexcart.order.dto.OrderRequest;
import com.apexcart.order.dto.OrderResponse;

import java.util.List;

public interface OrderService
{
    OrderResponse createOrder(OrderRequest request,String username);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
}

