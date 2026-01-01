package com.apex_cart.repository;

import com.apex_cart.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    public Optional<Inventory>findByProductCode(String productCode);
}
