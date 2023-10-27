package com.example.ordermanagementsystem.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderInput {
    private Long userId;
    private Long id;
    private List<OrderItemInput> items;
}