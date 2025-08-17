package com.example.transaction_assignment.batch.transaction;

import com.example.transaction_assignment.batch.converter.CustomConverters;
import com.example.transaction_assignment.domain.Transactions;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component

public class TrxRecordReader {

    @Value("${batch.input.file}")
    private Resource inputFile;

    /**
     * read data from txt file, it can be extended to extract data from S3 etc
     * @param conversionService
     * @return
     */
    public FlatFileItemReader<Transactions> getTransactions(ConversionService conversionService) {
        FlatFileItemReader<Transactions> reader = new FlatFileItemReader<>();
        reader.setResource(inputFile);
        reader.setLinesToSkip(1);

        DefaultLineMapper<Transactions> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer("|");
        tokenizer.setNames("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId");

        BeanWrapperFieldSetMapper<Transactions> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Transactions.class);

        DefaultConversionService localService = new DefaultConversionService();
        localService.addConverter(new CustomConverters.StringToLocalDateConverter());
        localService.addConverter(new CustomConverters.StringToLocalTimeConverter());
        fieldSetMapper.setConversionService(localService);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);

        return reader;
    }
}
