package com.example.transaction_assignment.Batch;

import com.example.transaction_assignment.batch.transaction.TrxRecordWriter;
import com.example.transaction_assignment.domain.Transactions;
import com.example.transaction_assignment.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TrxRecordWriterUnitTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TrxRecordWriter writer;

    private Transactions trx1;
    private Transactions trx2;

    @BeforeEach
    void setup() {
        trx1 = Transactions.builder()
                .accountNumber("123")
                .trxAmount(new BigDecimal("100"))
                .description("Deposit")
                .customerId(1L)
                .build();

        trx2 = Transactions.builder()
                .accountNumber("456")
                .trxAmount(new BigDecimal("200"))
                .description("Withdrawal")
                .customerId(2L)
                .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testWriterCallsRepositorySaveAll() throws Exception {
        Chunk<Transactions> chunk = new Chunk<>(List.of(trx1, trx2));

        writer.write(chunk);

        // Capture the argument passed to saveAll
        ArgumentCaptor<List<Transactions>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(transactionRepository).saveAll(captor.capture());

        List<Transactions> savedItems = captor.getValue();
        Assertions.assertEquals(2, savedItems.size());
        Assertions.assertTrue(savedItems.contains(trx1));
        Assertions.assertTrue(savedItems.contains(trx2));
    }

    @Test
    void testWriterEmptyChunk() throws Exception {
        Chunk<Transactions> emptyChunk = new Chunk<>(List.of());

        writer.write(emptyChunk);

        // Repository should still be called with empty list
        Mockito.verify(transactionRepository, Mockito.times(1)).saveAll(emptyChunk.getItems());
    }
}
