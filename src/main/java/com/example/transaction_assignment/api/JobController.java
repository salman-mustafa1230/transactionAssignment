package com.example.transaction_assignment.api;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importTransactionsJob;

    @PostMapping("/run")
    public String runBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", Long.valueOf(System.currentTimeMillis()))
                    .toJobParameters();

            jobLauncher.run(importTransactionsJob, jobParameters);
            return "Batch job has been invoked";
        } catch (Exception e) {
            return "Job failed: " + e.getMessage();
        }
    }
}
