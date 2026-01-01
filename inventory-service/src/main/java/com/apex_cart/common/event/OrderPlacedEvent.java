package com.apex_cart.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private String productCode;
    private Integer quantity;
    private String username;
    private String orderId;
}
