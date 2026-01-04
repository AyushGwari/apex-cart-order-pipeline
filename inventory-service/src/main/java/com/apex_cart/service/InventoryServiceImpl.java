package com.apex_cart.service;

import com.apex_cart.dto.InventoryRequest;
import com.apex_cart.entity.Inventory;
import com.apex_cart.exception.ProductNotFoundException;
import com.apex_cart.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;
    private final RedissonClient redissonClient;
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
        String lockKey = "lock:inventory:" + productCode;
        RLock lock = redissonClient.getLock(lockKey);
        try {
// Wait up to 10s for lock, lease for 5s (watchdog will extend if needed)
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);

            if (!isLocked) {
                // CRITICAL: Handle the case where the system is too busy to get a lock
                throw new RuntimeException("System busy: Could not acquire lock for " + productCode);
            }                inventoryRepository.findByProductCode(productCode)
                        .ifPresentOrElse(inventory -> {
                            if (inventory.getQuantity() >= quantity) {
                                inventory.setQuantity(inventory.getQuantity() - quantity);
                                inventoryRepository.save(inventory);
                                log.info("Stock successfully updated by {}. Remaining: {}", username, inventory.getQuantity());
                            } else {
                                log.warn("Atttempted to reduce stock below zero for {}", productCode);
                            }
                        }, () -> {
                            throw new ProductNotFoundException("Cannot reduce stock.Product " + productCode + " not found.");
                        });

        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock released for product: {}", productCode);
            }
        }
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
