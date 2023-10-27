package com.example.ordermanagementsystem.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemInput {
    private int quantity;
    private Long productId;
}
