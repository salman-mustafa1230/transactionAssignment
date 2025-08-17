package com.example.transaction_assignment.service;

import com.example.transaction_assignment.domain.Transactions;
import com.example.transaction_assignment.shared.mapper.TransactionMapper;
import com.example.transaction_assignment.shared.dto.TransactionResponse;
import com.example.transaction_assignment.repository.TransactionRepository;
import com.example.transaction_assignment.repository.TransactionSpecifications;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public Page<TransactionResponse> searchTransactions(Long customerId, String accountNumber, String description, Pageable pageable) {
        Specification<Transactions> spec = TransactionSpecifications.buildSpec(customerId, accountNumber, description);

        Page<Transactions> result = transactionRepository.findAll(spec, pageable);
        log.info("search transactions result: {}", result);
        return result.map(transactionMapper::toDto);
    }

    /**
     * utilised optimsitc lock but can utilise pessimistic locks as well for more control
     * @param id
     * @param newDescription
     * @return
     */
    @Transactional
    public Transactions updateDescription(Long id, String newDescription) {
        Transactions trx = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        trx.setDescription(newDescription);

        try {
            return transactionRepository.save(trx);
        } catch (OptimisticLockException e) {
            throw new IllegalStateException("Concurrent update detected. Please retry.", e);
        }
    }
}
