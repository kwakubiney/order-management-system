package com.example.ordermanagementsystem.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductInput {
    private Long id;
    private String name;
    private int stock;
    private double price;
}