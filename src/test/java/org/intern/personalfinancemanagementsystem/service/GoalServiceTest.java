package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.GoalRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.GoalResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Goal;
import org.intern.personalfinancemanagementsystem.domain.entity.GoalStatus;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.GoalRepository;
import org.intern.personalfinancemanagementsystem.service.impl.GoalServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GoalServiceImpl goalService;

    private UUID userId;
    private UUID goalId;

    private User user;
    private Goal goal;
    private GoalRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        goalId = UUID.randomUUID();

        user = User.builder().id(userId).build();

        goal = Goal.builder()
                .id(goalId)
                .user(user)
                .name("Mua laptop")
                .targetAmount(new BigDecimal("30000000"))
                .currentAmount(new BigDecimal("10000000"))
                .targetDate(LocalDate.of(2026, 12, 31))
                .goalStatus(GoalStatus.IN_PROGRESS)
                .build();

        request = new GoalRequest(
                "Mua Laptop", new BigDecimal("30000000"), LocalDate.of(2026, 12, 31)
        );
    }

    @Test
    void addGoal_whenValidRequest_shouldReturnSuccess() {
        when(userService.getReferenceById(userId)).thenReturn(user);

        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal savedGoal = invocation.getArgument(0);
            savedGoal.setId(goalId);
            return savedGoal;
        });

        UUID result = goalService.addGoal(userId, request);
        Assertions.assertNotNull(result);
        assertEquals(goalId, result);

        verify(userService).getReferenceById(userId);
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void getAllGoal_shouldReturnSuccess() {
        int pageNo = 0;
        int pageSize = 10;

        Page<Goal> page = new PageImpl<>(List.of(goal), PageRequest.of(pageNo, pageSize), 1);

        when(goalRepository.findGoalByUserId(eq(userId), eq(PageRequest.of(pageNo, pageSize)))).thenReturn(page);

        var result = goalService.getAllGoal(userId, pageNo, pageSize);

        Assertions.assertNotNull(result);
    }

    @Test
    void getGoalById_whenValidId_shouldReturnSuccess() {
        when(goalRepository.findByIdAndUserId(goalId, userId)).thenReturn(Optional.of(goal));

        GoalResponse result = goalService.getGoalById(userId, goalId);

        Assertions.assertNotNull(result);
        assertEquals(goalId, result.goalId());
        assertEquals("Mua laptop", result.name());
        assertEquals(new BigDecimal("20000000"), result.remainingAmount()
        );

        verify(goalRepository).findByIdAndUserId(goalId, userId);
    }

    @Test
    void updateGoal_whenValidRequest_shouldReturnSuccess() {
        when(goalRepository.findByIdAndUserId(goalId, userId)).thenReturn(Optional.of(goal));
        when(goalRepository.existsByNameAndUserIdAndIdNot(request.name(), userId, goalId)).thenReturn(false);

        goalService.updateGoalById(userId, goalId, request);

        assertEquals(request.targetDate(), goal.getTargetDate());
    }

    @Test
    void updateGoal_whenNameAlreadyExists_shouldThrowConflict() {
        when(goalRepository.findByIdAndUserId(goalId, userId)).thenReturn(Optional.of(goal));
        when(goalRepository.existsByNameAndUserIdAndIdNot(request.name(), userId, goalId)).thenReturn(true);

        AppException caughtException = assertThrows(AppException.class, () -> {
            goalService.updateGoalById(userId, goalId, request);
        });

        assertEquals(HttpStatus.CONFLICT, caughtException.getStatus());
    }

    @Test
    void updateGoal_whenGoalNotExist_shouldThrowException() {
        when(goalRepository.findByIdAndUserId(goalId, userId)).thenReturn(Optional.empty());

        AppException caughtException = assertThrows(AppException.class, () -> {
            goalService.updateGoalById(userId, goalId, request);
        });

        assertEquals(HttpStatus.NOT_FOUND, caughtException.getStatus());
    }

    @Test
    void softDeleteGoal_whenValid_shouldReturnSuccess() {
        when(goalRepository.softDeleteGoal(eq(goalId), any(Instant.class), eq(userId))).thenReturn(1);
        assertDoesNotThrow(() -> goalService.softDeleteGoalById(userId, goalId));
    }

    @Test
    void softDeleteGoal_whenInvalid_shouldThrowException() {
        when(goalRepository.softDeleteGoal(eq(goalId), any(Instant.class), eq(userId))).thenReturn(0);
        AppException exception = assertThrows(AppException.class, () -> goalService.softDeleteGoalById(userId, goalId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}