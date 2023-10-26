package com.example.ordermanagementsystem.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductInput {
    private Long id;
    private String name;
    private int stock;
    private double price;
}