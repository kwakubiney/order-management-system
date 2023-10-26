package com.example.ordermanagementsystem.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPayload {
    private Long id;
    private String name;
    private String email;
}