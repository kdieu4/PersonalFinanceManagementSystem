package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.GoalRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.GoalResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.*;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.GoalRepository;
import org.intern.personalfinancemanagementsystem.service.GoalService;
import org.intern.personalfinancemanagementsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class GoalServiceImpl implements GoalService {
    GoalRepository goalRepository;
    UserService userService;

    @Override
    @Transactional
    public UUID addGoal(UUID userId, GoalRequest request) {
        User user = userService.getReferenceById(userId);
        Goal goal = Goal.builder()
                .name(request.name())
                .user(user)
                .targetAmount(request.targetAmount())
                .currentAmount(BigDecimal.ZERO)
                .targetDate(request.targetDate())
                .goalStatus(GoalStatus.IN_PROGRESS)
                .build();
        goal = goalRepository.save(goal);
        return goal.getId();
    }

    @Override
    public PageResponse<List<GoalResponse>> getAllGoal(UUID userId, int pageNo, int pageSize) {
        Page<Goal> page = goalRepository.findGoalByUserId(userId, PageRequest.of(pageNo, pageSize));
        List<GoalResponse> list = page.stream().map(this::buildGoalResponse).toList();
        return new PageResponse<>(pageNo, pageSize, page.getTotalPages(), list);
    }

    private GoalResponse buildGoalResponse(Goal goal) {
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(goal.getCurrentAmount());

        return GoalResponse.from(goal, remainingAmount);
    }

    @Override
    public GoalResponse getGoalById(UUID userId, UUID goalId) {
        Goal goal = findByIdAndUserId(goalId, userId);
        return buildGoalResponse(goal);
    }

    private Goal findByIdAndUserId(UUID goalId, UUID userId) {
        return goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Goal.GOAL_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
    }

    @Override
    @Transactional
    public void updateGoalById(UUID userId, UUID goalId, GoalRequest request) {
        Goal goal = findByIdAndUserId(goalId, userId);
        if (goalRepository.existsByNameAndUserIdAndIdNot(request.name(), userId, goalId)) {
            throw new AppException(HttpStatus.CONFLICT, ErrorMessage.Goal.GOAL_EXISTED, ErrorMessage.CONFLICT_CODE);
        }
        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        goal.setTargetDate(request.targetDate());
        log.info("Goal has updated successfully, goal_id={}", goal.getId());
    }

    @Override
    @Transactional
    public void softDeleteGoalById(UUID userId, UUID goalId) {
        int updated = goalRepository.softDeleteGoal(goalId, Instant.now(), userId);
        log.info("Goal has deleted successfully, goal_id={}, updated_row={}", goalId, updated);
        if (updated == 0) {
            throw new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Goal.GOAL_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE
            );
        }
    }
}