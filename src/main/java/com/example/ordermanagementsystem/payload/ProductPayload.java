package com.example.ordermanagementsystem.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductPayload {
    private Long id;
    private String name;
    private int stock;
    private double price;
}
