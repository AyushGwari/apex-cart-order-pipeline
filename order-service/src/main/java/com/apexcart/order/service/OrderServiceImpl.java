package com.apexcart.order.service;

import com.apexcart.order.dto.OrderRequest;
import com.apexcart.order.dto.OrderResponse;
import com.apexcart.order.entity.Order;
import com.apexcart.order.exception.OrderNotFoundException;
import com.apexcart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements  OrderService{
    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request,String username) {
         Order order = new Order();
         order.setProduct(request.getProduct());
         order.setPrice(request.getPrice());
         order.setStatus("CONFIRMED");
         order.setUsername(username);
         Order saveOrder  = orderRepository.save(order);
         return mapToResponse(saveOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setProduct(order.getProduct());
        orderResponse.setTotalPrice(order.getPrice());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setOrderDate(order.getCreatedAt());
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(()-> new OrderNotFoundException(id));
        return mapToResponse(order);
    }
    public List<OrderResponse> getOrderForUsers(String username){
        return orderRepository.findByUsername(username).stream().map(this::mapToResponse).collect(Collectors.toList());
    }
}
