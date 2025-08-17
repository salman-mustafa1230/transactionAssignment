package com.example.transaction_assignment.Batch;

import com.example.transaction_assignment.domain.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionReaderTest {
    private FlatFileItemReader<Transactions> reader;
    @BeforeEach
    void setup() {
        // Initialize reader
        reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("transaction-test.txt"));
        reader.setLinesToSkip(1); // skip header

        // Tokenizer
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer("|");
        tokenizer.setNames("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId");

        // FieldSetMapper
        BeanWrapperFieldSetMapper<Transactions> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Transactions.class);

        // ConversionService for LocalDate/LocalTime
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(String.class, LocalDate.class, LocalDate::parse);
        conversionService.addConverter(String.class, LocalTime.class, LocalTime::parse);

        fieldSetMapper.setConversionService(conversionService);

        // LineMapper
        DefaultLineMapper<Transactions> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
    }

    @Test
    void testReaderReadsFirstRecord() throws Exception {
        reader.open(new ExecutionContext());

        Transactions trx = reader.read();

        assertNotNull(trx);
        assertEquals("8872838283", trx.getAccountNumber());
        assertEquals(new BigDecimal("123.00"), trx.getTrxAmount());
        assertEquals("FUND TRANSFER", trx.getDescription());
        assertEquals(LocalDate.parse("2019-09-12"), trx.getTrxDate());
        assertEquals(LocalTime.parse("11:11:11"), trx.getTrxTime());
        assertEquals(222L, trx.getCustomerId());

        reader.close();
    }

    @Test
    void testReaderReadsAllRecords() throws Exception {
        reader.open(new ExecutionContext());

        Transactions trx1 = reader.read();
        Transactions trx2 = reader.read();
        Transactions trx3 = reader.read(); // should be null at end of file

        assertNotNull(trx1);
        assertNotNull(trx2);
        assertNull(trx3); // End of file

        reader.close();
    }
}
