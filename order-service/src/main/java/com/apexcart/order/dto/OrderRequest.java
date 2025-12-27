package com.apexcart.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank(message = "Product name is required")
    private String product;

    @NotNull(message = "Price is required")
    @Min(value = 1,message = "Price must be greater than 0")
    private Double price;

    private Integer quantity;
}
