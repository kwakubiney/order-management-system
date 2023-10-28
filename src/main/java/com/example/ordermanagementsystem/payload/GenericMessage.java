package com.example.ordermanagementsystem.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GenericMessage {
    private String message;
}
