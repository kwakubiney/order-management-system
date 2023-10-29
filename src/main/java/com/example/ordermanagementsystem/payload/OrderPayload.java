package com.example.ordermanagementsystem.payload;

import com.example.ordermanagementsystem.entity.ProductLine;
import com.example.ordermanagementsystem.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderPayload {
    private Long id;
    private List<ProductLine> products;
    private User user;
}