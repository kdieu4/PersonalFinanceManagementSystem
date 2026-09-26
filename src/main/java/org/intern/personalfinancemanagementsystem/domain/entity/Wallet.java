package org.intern.personalfinancemanagementsystem.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("archived_at IS NULL")
public class Wallet extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, precision = 50, scale = 2)
    @Builder.Default
    BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    String currency;

    @Column(name = "archived_at")
    Instant archivedAt;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    List<Transaction> transactions;
}
