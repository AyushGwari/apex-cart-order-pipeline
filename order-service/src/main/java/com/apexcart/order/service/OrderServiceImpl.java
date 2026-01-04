package com.apexcart.order.service;

import com.apexcart.order.client.InventoryClient;
import com.apexcart.order.common.event.OrderPlacedEvent;
import com.apexcart.order.dto.OrderRequest;
import com.apexcart.order.dto.OrderResponse;
import com.apexcart.order.entity.Order;
import com.apexcart.order.exception.InsufficientStockException;
import com.apexcart.order.exception.OrderNotFoundException;
import com.apexcart.order.kafka.OrderProducer;
import com.apexcart.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements  OrderService{
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderProducer orderProducer;
    @Override
    @Transactional
    @CircuitBreaker(name = "inventory-service",fallbackMethod = "inventoryFallback")
    public OrderResponse createOrder(OrderRequest request,String username) {
        log.info("Processing order for user: {} and product: {}", username, request.getProductCode());
        boolean isAvailable = inventoryClient.ItemisInStock(request.getProductCode(), request.getQuantity(),username);
        if(!isAvailable){
            log.warn("Order failed: Insufficient stock for {}", request.getProductCode());
            throw new InsufficientStockException("Product " + request.getProductCode() + " is out of stock.");
        }
         Order order = new Order();
         order.setProductCode(request.getProductCode());
         order.setPrice(request.getPrice());
         order.setStatus("PLACED    ");
         order.setQuantity(request.getQuantity());
         order.setUsername(username);
         Order saveOrder  = orderRepository.save(order);
        OrderPlacedEvent event = new OrderPlacedEvent(
                saveOrder.getProductCode(),
                saveOrder.getQuantity(),
                saveOrder.getUsername(),
                saveOrder.getId().toString()
        );
        orderProducer.sendMessage(event);
        log.info("Order successfully placed with ID: {}", saveOrder.getId());

         return mapToResponse(saveOrder);
    }

    public OrderResponse inventoryFallback(OrderRequest request,String username){
        log.error("Circuit breaker Triggered ,service down or slow");
        OrderResponse res = new OrderResponse();
        res.setStatus("CIRCUIT_OPEN");
        res.setProduct(request.getProductCode());
        return res;
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setProduct(order.getProductCode());
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
