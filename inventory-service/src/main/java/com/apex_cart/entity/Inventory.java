package com.apex_cart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String productCode;

    @Column(nullable = false)
    private Integer quantity;

    @Version  //prevents race condition during concurrent stock updates
    //this is optimistic locking
    private Integer version;
}
