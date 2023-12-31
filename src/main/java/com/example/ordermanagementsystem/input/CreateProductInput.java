package com.example.ordermanagementsystem.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductInput {
    private String name;
    private int stock;
    private double price;
}