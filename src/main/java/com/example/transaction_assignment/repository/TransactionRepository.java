package com.example.transaction_assignment.repository;

import com.example.transaction_assignment.domain.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long>,
        JpaSpecificationExecutor<Transactions> {
    Page<Transactions> findByCustomerId(Long customerId, Pageable pageable);

    Page<Transactions> findByAccountNumberContaining(String accountNumber, Pageable pageable);

    Page<Transactions> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

}
