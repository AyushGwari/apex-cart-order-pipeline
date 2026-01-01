package com.apex_cart.service;

import com.apex_cart.dto.InventoryRequest;

public interface InventoryService {
    boolean isStock(String productCode,Integer requestQuantity,String username);
    void reduceStock(String productCode ,Integer quantity,String username);
    void addStock(InventoryRequest request, String adminUser);
}
