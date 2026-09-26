package org.intern.personalfinancemanagementsystem.service;

import jakarta.validation.Valid;
import org.intern.personalfinancemanagementsystem.domain.dto.request.GoalRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.GoalResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

public interface GoalService {
    UUID addGoal(UUID userId, GoalRequest request);

    PageResponse<List<GoalResponse>> getAllGoal(UUID userId, int pageNo, int pageSize);

    GoalResponse getGoalById(UUID id, UUID goalId);

    void updateGoalById(UUID id, UUID goalId, @Valid GoalRequest request);

    void softDeleteGoalById(UUID id, UUID goalId);
}
