package com.apex_cart.controller;

import com.apex_cart.dto.InventoryRequest;
import com.apex_cart.service.InventoryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/check")
    public ResponseEntity<Boolean>ItemisInStock(@RequestParam @NotBlank String productCode,
                                                @RequestParam @Min(1) Integer quantity,@RequestHeader("loggedInUser") String username){
        return ResponseEntity.ok(inventoryService.isStock(productCode,quantity,username));
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<String> addStock(@RequestBody InventoryRequest request,@AuthenticationPrincipal String adminUser) {
        inventoryService.addStock(request,adminUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("Stock updated successfully");
    }
}
