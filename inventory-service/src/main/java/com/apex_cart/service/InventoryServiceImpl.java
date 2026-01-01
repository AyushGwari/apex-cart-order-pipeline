package com.apex_cart.service;

import com.apex_cart.dto.InventoryRequest;
import com.apex_cart.entity.Inventory;
import com.apex_cart.exception.ProductNotFoundException;
import com.apex_cart.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;
    @Override
    @Transactional(readOnly = true)
    public boolean isStock(String productCode, Integer requestQuantity,String username) {
        log.info("Stock check initiated by user: {} for product: {}", username, productCode);
        return inventoryRepository.findByProductCode(productCode).map(inventory -> inventory.getQuantity() >= requestQuantity).orElseThrow(()->
                new ProductNotFoundException("Product "+productCode+"not found."));
    }

    @Override
    @Transactional
    public void reduceStock(String productCode, Integer quantity,String username) {
        log.info("Stock reduction for product: {} requested by user: {}", productCode, username);
        inventoryRepository.findByProductCode(productCode)
                .ifPresentOrElse(inventory -> {
                    if(inventory.getQuantity()>=quantity){
                        inventory.setQuantity(inventory.getQuantity() - quantity);
                        inventoryRepository.save(inventory);
                        log.info("Stock successfully updated by {}. Remaining: {}", username, inventory.getQuantity());                    }
                    else{
                        log.warn("Atttempted to reduce stock below zero for {}",productCode);
                    }
                },()->{
                    throw new ProductNotFoundException("Cannot reduce stock.Product "+productCode +" not found.");
                });
    }

    @Override
    @Transactional
    public void addStock(InventoryRequest request, String adminUser) {
        log.info("Admin {} is adding {} units to product {}", adminUser, request.getQuantity(), request.getProductCode());
        inventoryRepository.findByProductCode(request.getProductCode())
                .ifPresentOrElse(
                        inventory -> {
                            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
                            inventoryRepository.save(inventory);
                            log.info("Updated existing stock. New total: {}", inventory.getQuantity());
                        },()->{
                            Inventory newInventory = Inventory.builder()
                                    .productCode(request.getProductCode())
                                    .quantity(request.getQuantity())
                                    .build();
                            inventoryRepository.save(newInventory);
                            log.info("Created new inventory record for product {}", request.getProductCode());
                        }
                );

    }
}
