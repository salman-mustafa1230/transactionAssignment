package com.example.transaction_assignment.api;

import com.example.transaction_assignment.domain.Transactions;
import com.example.transaction_assignment.shared.dto.TransactionResponse;
import com.example.transaction_assignment.shared.dto.TransactionUpdateRequest;
import com.example.transaction_assignment.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    // 1️⃣ Retrieve transactions with search & pagination
    @GetMapping
    @Operation(summary = "Get transactions", description = "Retrieve transactions with filtering and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public Page<TransactionResponse> getTransactions(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionService.searchTransactions(customerId, accountNumber, description, pageable);
    }

    // 2️⃣ Update transaction description
    @PatchMapping("/{id}/description")
    @Operation(summary = "Update transaction description", description = "Update description with optimistic locking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "409", description = "Optimistic lock conflict")
    })
    public Transactions updateDescription(
            @PathVariable Long id,
            @RequestBody @Valid TransactionUpdateRequest request
    ) {
        return transactionService.updateDescription(id, request.getDescription());
    }
    
}
