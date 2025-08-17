package com.example.transaction_assignment.batch;

import com.example.transaction_assignment.batch.transaction.TrxRecordReader;
import com.example.transaction_assignment.batch.transaction.TrxRecordWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.transaction_assignment.batch.transaction.TrxRecordProcessor;
import com.example.transaction_assignment.domain.Transactions;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@Slf4j
public class BatchConfiguration {


    private final TrxRecordWriter trxRecordWriter;
    private final TrxRecordReader trxRecordReader;
    private final TrxRecordProcessor transactionItemProcessor;

    @Bean
    public DataSourceInitializer batchDataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("org/springframework/batch/core/schema-h2.sql")); // only for h2 db

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    // Step
    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, ConversionService conversionService) {
        return new StepBuilder("file-to-db", jobRepository)
                .<Transactions, Transactions>chunk(10, transactionManager)
                .reader(trxRecordReader.getTransactions(conversionService))
                .processor(transactionItemProcessor)
                .writer(trxRecordWriter)
                .faultTolerant()
                .retryLimit(3)  // retry 3 times
                .retry(ConcurrencyFailureException.class) // retry on DB deadlock
                .skipLimit(5)   // skip up to 5 bad records
                .skip(Exception.class) // skip specific exception
                .listener(stepExecutionListener()) // log successful or error response
                .build();
    }

    // Job
    @Bean
    public Job importTransactionsJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importTransactionsJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                log.info("Step starting: {}", stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                if(stepExecution.getFailureExceptions().isEmpty()) {
                    log.info("Step completed successfully: {}", stepExecution.getStepName());
                } else {
                    // can create monitor and alert to investigate later
                    stepExecution.getFailureExceptions().forEach(ex -> log.error("Step failed", ex));
                }
                return stepExecution.getExitStatus();
            }
        };
    }
}
