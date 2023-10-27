package com.example.ordermanagementsystem.Dto;

import com.example.ordermanagementsystem.entity.ProductLine;
import com.example.ordermanagementsystem.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderPayload {
    private Long id;
    private List<ProductLine> products;
    private User user;
}