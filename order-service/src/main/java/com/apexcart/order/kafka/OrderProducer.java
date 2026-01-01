package com.apexcart.order.kafka;

import com.apexcart.order.common.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderProducer {
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void sendMessage(OrderPlacedEvent event){
        log.info("Sending OrderPlacedEvent to Kafka for Order ID: {}", event.getOrderId());
        CompletableFuture<SendResult<String, OrderPlacedEvent>> future =
                kafkaTemplate.send("order-placed-topic", event);
        future.whenComplete((result,ex)->{
            if(ex == null){
                log.info("Sent message=[{}] with offset=[{}]", event, result.getRecordMetadata().offset());
            }else{
                log.error("Unable to send message=[{}] due to: {}", event, ex.getMessage());
            }
        });
    }
}
