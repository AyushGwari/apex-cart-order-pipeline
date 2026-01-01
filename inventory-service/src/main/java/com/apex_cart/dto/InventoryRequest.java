package com.apex_cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequest {
    @NotBlank(message = "Product code can not be blank")
    private String productCode;

    @NotNull(message = "Quantity is mandatory")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}
