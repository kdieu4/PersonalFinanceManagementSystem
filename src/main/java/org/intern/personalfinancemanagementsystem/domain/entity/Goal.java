package org.intern.personalfinancemanagementsystem.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "goals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("archived_at IS NULL")
public class Goal extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, precision = 19, scale = 2, name = "current_amount")
    @Builder.Default
    BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2, name = "target_amount")
    BigDecimal targetAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    GoalStatus goalStatus = GoalStatus.IN_PROGRESS;

    @Column(name = "target_date", nullable = false)
    LocalDate targetDate;

    @Column(name = "archived_at")
    Instant archivedAt;
}
