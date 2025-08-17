package com.example.transaction_assignment.service;

import com.example.transaction_assignment.domain.Transactions;
import com.example.transaction_assignment.shared.dto.TransactionResponse;
import com.example.transaction_assignment.shared.mapper.TransactionMapper;
import com.example.transaction_assignment.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void searchTransactions_returnsMappedPage() {
        Transactions trx = Transactions.builder()
                .id(1L)
                .accountNumber("123")
                .customerId(222L)
                .description("Test")
                .trxAmount(BigDecimal.valueOf(123.0))
                .trxDate(LocalDate.parse("2025-08-17"))
                .trxTime(LocalTime.parse("12:00:00"))
                .build();

        TransactionResponse dto = TransactionResponse.builder()
                .id(trx.getId())
                .accountNumber(trx.getAccountNumber())
                .customerId(trx.getCustomerId())
                .description(trx.getDescription())
                .trxAmount(trx.getTrxAmount())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Transactions> page = new PageImpl<>(List.of(trx));

        Mockito.when(transactionRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageable)))
                .thenReturn(page);

        Mockito.when(transactionMapper.toDto(Mockito.any(Transactions.class))).thenReturn(dto);

        Page<TransactionResponse> result = transactionService.searchTransactions(222L, "123", "Test", pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Test", result.getContent().getFirst().getDescription());
        Assertions.assertEquals(1L, result.getContent().getFirst().getId());

        Mockito.verify(transactionRepository).findAll(Mockito.any(Specification.class), Mockito.eq(pageable));
        Mockito.verify(transactionMapper).toDto(trx);
    }

    @Test
    void searchTransactions_withoutSearchParam_returnAllTransactions() {
        List<Transactions> transactions = List.of(
                Transactions.builder().id(1L).accountNumber("123").customerId(222L)
                        .description("Test1").trxAmount(BigDecimal.valueOf(100.0))
                        .trxDate(LocalDate.parse("2025-08-17"))
                        .trxTime(LocalTime.parse("12:00:00")).build(),
                Transactions.builder().id(2L).accountNumber("124").customerId(223L)
                        .description("Test2").trxAmount(BigDecimal.valueOf(200.0))
                        .trxDate(LocalDate.parse("2025-08-17"))
                        .trxTime(LocalTime.parse("13:00:00")).build()
        );

        TransactionResponse dto1 = TransactionResponse.builder().id(1L).description("Test1").build();
        TransactionResponse dto2 = TransactionResponse.builder().id(2L).description("Test2").build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Transactions> page = new PageImpl<>(transactions);

        Mockito.when(transactionRepository.findAll(Mockito.isNull(Specification.class), Mockito.eq(pageable)))
                .thenReturn(page);

        Mockito.when(transactionMapper.toDto(Mockito.any(Transactions.class)))
                .thenReturn(dto1, dto2);

        Page<TransactionResponse> result = transactionService.searchTransactions(null, null, null, pageable);

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals(2, result.getContent().size());
        Mockito.verify(transactionMapper, Mockito.times(2)).toDto(Mockito.any(Transactions.class));
    }


    // update transaction test
    @Test
    void updateDescription_successfulUpdate_returnsUpdatedTransaction() {
        Transactions trx = new Transactions();
        trx.setId(1L);
        trx.setDescription("Old");
        trx.setVersion(0L);

        Mockito.when(transactionRepository.findById(1L)).thenReturn(Optional.of(trx));
        Mockito.when(transactionRepository.save(trx)).thenReturn(trx);

        Transactions updated = transactionService.updateDescription(1L, "New");

        Assertions.assertEquals(updated.getDescription(), "New");
        Mockito.verify(transactionRepository).save(trx);
    }

    @Test
    void updateDescription_transactionNotFound_throwsException() {
        Mockito.when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.updateDescription(1L, "New")
        );

        Assertions.assertTrue(exception.getMessage().contains("Transaction not found"));
        Assertions.assertEquals("Transaction not found", exception.getMessage());
    }

    @Test
    void updateDescription_transactionNotFound_throwsException_Standard() {
        // Arrange
        Mockito.when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.updateDescription(1L, "New"),
                "Should throw IllegalArgumentException when transaction not found"
        );

        Assertions.assertEquals("Transaction not found", exception.getMessage());
    }
}
