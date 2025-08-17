package com.example.transaction_assignment.shared.mapper;

import com.example.transaction_assignment.domain.Transactions;
import com.example.transaction_assignment.shared.dto.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionResponse toDto(Transactions trx) {
        return TransactionResponse.builder()
                .id(trx.getId())
                .accountNumber(trx.getAccountNumber())
                .trxAmount(trx.getTrxAmount())
                .description(trx.getDescription())
                .trxDate(trx.getTrxDate())
                .trxTime(trx.getTrxTime())
                .customerId(trx.getCustomerId())
                .build();
    }
}
