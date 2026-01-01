package com.apexcart.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {
    @GetMapping("/api/v1/inventory/check")
    boolean ItemisInStock(@RequestParam("productCode")String productCode,
                      @RequestParam("quantity")Integer quantity,
                        @RequestHeader("loggedInUser") String username);
}
