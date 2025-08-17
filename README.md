# AssTransaction Application

## Overview
The **AssTransaction Application** is a Spring Boot–based system that provides both REST APIs and batch processing capabilities.  
It is designed using a **layered architecture** to ensure separation of concerns, maintainability, and scalability.

---

## Architecture

### Layered Structure
The project follows a **layered architecture**.

---

## Key Design Decisions

- **Separation of Concerns**
    - Controllers only handle HTTP requests/responses.
    - Services contain business logic.
    - Repositories manage persistence.

- **Batch Support**
    - Spring Batch is used to handle bulk transaction processing.
    - Batch jobs are decoupled from REST APIs for flexibility.

- **Shared Module**
    - Introduced a `shared/` folder to hold common utilities, enums, DTOs, and mappers that are reused across services and batch jobs.

- **Exception Handling**
    - Centralized using `@ControllerAdvice` for consistent API error responses.


---

## Technology Stack

- **Java 21**
- **Spring Boot 3.x**
- **Spring Data JPA (Hibernate)**
- **Spring Batch**
- **H2 / PostgreSQL (configurable)**
- **Lombok** to reduce boilerplate code
- **Maven** for dependency management

---

## Running the Application

1. **Build the project**
   ```bash
   mvn clean install
2. Run the Spring boot app
    ```bash
   mvn spring-boot:run
3. Api Documentation
    ```bash
   http://localhost:8080/swagger-ui/index.html

# Batch Jobs

1. Configured under batch/ package.
2. Jobs can be triggered via [api/batch/run](http://localhost:8080/swagger-ui/index.html#/job-controller/runBatchJob) endpoint.
3. Used custom converter for date and time as its not available by default.
4. Spring batch configurations are in BatchConfiguration.java file.
5. Used flyway migrations for creating the transaction table.
6. **Example:** Transaction Batch Job reads transactions from txt file in chunks, processes them, and writes results to DB in chunks as well.

# Future proofing
1. We can separate out the Batch job so it can use separate resource based on scalability needs.
2. CI/CD pipeline integration with Docker & Kubernetes.
3. Enable Actuator to monitor metrics or can be monitored by other observability tools like Datadog.

## Architecture

### Class Diagram
```mermaid
classDiagram
    class BatchConfiguration {
        +importTransactionsJob() : Job
        +step1() : Step
        +batchDataSourceInitializer(): DataSourceInitializer
        +stepExecutionListener(): StepExecutionListener
    }
    class TrxRecrodReader {
        +getTransactions() : FlatFileItemReader<Transactions>
    }
    class TrxRecordProcessor {
        +process(Transaction) : Transactions
    }
    class TrxRecordWriter {
        +write(Chunk<? extends Transactions> chunk)
    }

    BatchConfiguration --> TrxRecrodReader
    BatchConfiguration --> TrxRecordProcessor
    BatchConfiguration --> TrxRecordWriter


