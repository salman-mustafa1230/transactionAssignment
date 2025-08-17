package com.example.transaction_assignment.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransactionUpdateRequest {
    @NotBlank(message = "Description cannot be empty")
    private String description;
}
