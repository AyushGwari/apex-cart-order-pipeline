package com.apex_cart.kafka;

import com.apex_cart.common.event.OrderPlacedEvent;
import com.apex_cart.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryConsumer {
    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-placed-topic",groupId = "inventory-group")
    public void consumerOrderPlacedEvent(OrderPlacedEvent event){
        log.info("Kafka Message Received: Order ID {} for Product {}",
                event.getOrderId(), event.getProductCode());
        try {
            // We pass the username from the event for the audit trail we built earlier
            inventoryService.reduceStock(
                    event.getProductCode(),
                    event.getQuantity(),
                    event.getUsername()
            );

            log.info("Inventory successfully updated for Order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to update inventory for Order ID: {}. Error: {}",
                    event.getOrderId(), e.getMessage());
        }
    }
}
