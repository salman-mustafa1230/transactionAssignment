package com.example.transaction_assignment.batch.transaction;

import com.example.transaction_assignment.domain.Transactions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrxRecordProcessor implements ItemProcessor<Transactions, Transactions> {

    @Override
    public Transactions process(Transactions transaction) {
        // can add validation here
        if (transaction.getAccountNumber() == null || transaction.getAccountNumber().isBlank()) {
            log.warn("Skipping record due to missing account number: {}", transaction);
            return null; // this record will be filtered out
        }
        // can add more logic to see if record already exists then can simply return the updated transaction
        return transaction;
    }
}
