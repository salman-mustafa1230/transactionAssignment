package com.example.transaction_assignment.repository;

import com.example.transaction_assignment.domain.Transactions;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecifications {
    public static Specification<Transactions> buildSpec(Long customerId, String accountNumber, String description) {
        Specification<Transactions> spec = null;

        if (customerId != null) {
            spec = hasCustomerId(customerId);
        }

        if (accountNumber != null && !accountNumber.isBlank()) {
            Specification<Transactions> accountSpec = accountNumberContains(accountNumber);
            spec = (spec == null) ? accountSpec : spec.and(accountSpec);
        }

        if (description != null && !description.isBlank()) {
            Specification<Transactions> descSpec = descriptionContains(description);
            spec = (spec == null) ? descSpec : spec.and(descSpec);
        }

        return spec;
    }

    public static Specification<Transactions> hasCustomerId(Long customerId) {
        return (root, query, cb) -> cb.equal(root.get("customerId"), customerId);
    }

    public static Specification<Transactions> accountNumberContains(String accountNumber) {
        return (root, query, cb) -> cb.like(root.get("accountNumber"), "%" + accountNumber + "%");
    }

    public static Specification<Transactions> descriptionContains(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }
}
