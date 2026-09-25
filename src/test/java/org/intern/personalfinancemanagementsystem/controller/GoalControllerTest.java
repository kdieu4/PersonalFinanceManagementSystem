package org.intern.personalfinancemanagementsystem.controller;

import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.GoalRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.GoalResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {
    @Mock
    private GoalService goalService;
    @Mock
    private CustomUserDetails principal;
    @Mock
    private GoalRequest request;
    @InjectMocks
    private GoalController goalController;

    private UUID userId;
    private UUID goalId;

    @BeforeEach
    void setUp() {
        goalController = new GoalController(goalService);
        userId = UUID.randomUUID();
        goalId = UUID.randomUUID();
        when(principal.getId()).thenReturn(userId);
    }

    @Test
    void addGoal_whenValidRequest_shouldReturnCreated() {
        UUID createdGoalId = UUID.randomUUID();
        when(goalService.addGoal(userId, request)).thenReturn(createdGoalId);
        ResponseEntity<ApiResponse<UUID>> response = goalController.addGoal(principal, request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(goalService).addGoal(userId, request);
    }

    @Test
    void getAllGoal_whenValidRequest_shouldReturnOk() {
        int pageNo = 0;
        int pageSize = 10;
        PageResponse<List<GoalResponse>> serviceResponse = mock(PageResponse.class);
        when(goalService.getAllGoal(userId, pageNo, pageSize)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<PageResponse<List<GoalResponse>>>> response = goalController.getAllGoal(principal, pageNo, pageSize);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(goalService).getAllGoal(userId, pageNo, pageSize);
    }

    @Test
    void getGoalById_whenGoalExists_shouldReturnOk() {
        GoalResponse serviceResponse = mock(GoalResponse.class);
        when(goalService.getGoalById(userId, goalId)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<GoalResponse>> response = goalController.getGoalById(principal, goalId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(goalService).getGoalById(userId, goalId);
    }

    @Test
    void updateGoalById_whenValidRequest_shouldReturnOk() {
        ResponseEntity<ApiResponse<Void>> response = goalController.updateGoalById(principal, goalId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(goalService).updateGoalById(userId, goalId, request);
    }

    @Test
    void deleteGoalById_whenGoalExists_shouldReturnOk() {
        ResponseEntity<ApiResponse<Void>> response = goalController.deleteGoalById(principal, goalId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(goalService).softDeleteGoalById(userId, goalId);
    }
}
