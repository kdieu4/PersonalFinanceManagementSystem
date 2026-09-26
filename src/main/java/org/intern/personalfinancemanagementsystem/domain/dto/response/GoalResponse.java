package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.Goal;
import org.intern.personalfinancemanagementsystem.domain.entity.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GoalResponse(
        UUID goalId,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        GoalStatus status,
        LocalDate targetDate
) {
    public static GoalResponse from(Goal goal, BigDecimal remainingAmount) {
        return new GoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                remainingAmount,
                goal.getGoalStatus(),
                goal.getTargetDate());
    }
}
