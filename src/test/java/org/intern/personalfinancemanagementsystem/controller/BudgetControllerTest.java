package org.intern.personalfinancemanagementsystem.controller;

import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.BudgetRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.BudgetResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.BudgetService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BudgetControllerTest {
    @Mock
    private BudgetService budgetService;
    @Mock
    private CustomUserDetails principal;
    @Mock
    private BudgetRequest request;
    @InjectMocks
    private BudgetController budgetController;

    private UUID userId;
    private UUID budgetId;

    @BeforeEach
    void setUp() {
        budgetController = new BudgetController(budgetService);
        userId = UUID.randomUUID();
        budgetId = UUID.randomUUID();
        when(principal.getId()).thenReturn(userId);
    }

    @Test
    void addBudget_whenValidRequest_shouldReturnCreated() {
        UUID createdBudgetId = UUID.randomUUID();
        when(budgetService.addBudget(userId, request)).thenReturn(createdBudgetId);
        ResponseEntity<ApiResponse<UUID>> response = budgetController.addBudget(principal, request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(budgetService).addBudget(userId, request);
    }

    @Test
    void getAllBudget_whenValidRequest_shouldReturnOk() {
        int pageNo = 0;
        int pageSize = 10;
        PageResponse<List<BudgetResponse>> serviceResponse = mock(PageResponse.class);
        when(budgetService.getAllBudget(userId, pageNo, pageSize)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<PageResponse<List<BudgetResponse>>>> response = budgetController.getAllBudget(principal, pageNo, pageSize);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(budgetService).getAllBudget(userId, pageNo, pageSize);
    }

    @Test
    void getBudgetById_whenBudgetExists_shouldReturnOk() {
        BudgetResponse serviceResponse = mock(BudgetResponse.class);
        when(budgetService.getBudgetById(userId, budgetId)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<BudgetResponse>> response = budgetController.getBudgetById(principal, budgetId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(budgetService).getBudgetById(userId, budgetId);
    }

    @Test
    void updateBudgetById_whenValidRequest_shouldReturnOk() {
        ResponseEntity<ApiResponse<Void>> response = budgetController.updateBudgetById(principal, budgetId, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(budgetService).updateBudgetById(userId, budgetId, request);
    }

    @Test
    void deleteBudgetById_whenBudgetExists_shouldReturnOk() {
        ResponseEntity<ApiResponse<Void>> response = budgetController.deleteBudgetById(principal, budgetId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(budgetService).softDeleteBudgetById(userId, budgetId);
    }
}

