package org.intern.personalfinancemanagementsystem.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("archived_at IS NULL")
public class Transaction {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(nullable = false)
    String purpose;

    @Column(nullable = false, precision = 50, scale = 2)
    @Builder.Default
    BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TransactionType type = TransactionType.EXPENSE;

    @Column(name = "archived_at")
    Instant archivedAt;
}
