package com.example.transaction_assignment.batch.transaction;

import com.example.transaction_assignment.domain.Transactions;
import com.example.transaction_assignment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrxRecordWriter implements ItemWriter<Transactions> {

    private final TransactionRepository transactionRepository;

    @Override
    public void write(Chunk<? extends Transactions> chunk) {
        transactionRepository.saveAll(chunk.getItems());
    }
}
