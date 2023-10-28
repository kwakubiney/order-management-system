package com.example.ordermanagementsystem.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductPayload {
    private Long id;
    private String name;
    private int stock;
    private double price;
}
