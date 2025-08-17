package com.example.transaction_assignment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

//  @Column(name = "account_number", nullable = false, length = 20)
  private String accountNumber;

//  @Column(name = "trx_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal trxAmount;

//  @Column(name = "description", nullable = false)
  private String description;

//  @Column(name = "trx_date", nullable = false)
  private LocalDate trxDate;

//  @Column(name = "trx_time", nullable = false)
  private LocalTime trxTime;

//  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Version
  private Long version;

//  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

//  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();

  @PreUpdate
  void onUpdate() { this.updatedAt = Instant.now();}
}
