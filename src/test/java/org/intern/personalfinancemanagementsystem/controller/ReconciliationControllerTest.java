package org.intern.personalfinancemanagementsystem.controller;

import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.response.ReconciliationResponse;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.ReconciliationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReconciliationControllerTest {
    @Mock
    private ReconciliationService reconciliationService;
    @Mock
    private CustomUserDetails principal;
    private ReconciliationController reconciliationController;
    private UUID userId;
    private UUID walletId;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        reconciliationController = new ReconciliationController(reconciliationService);
        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();
        startDate = LocalDate.of(2026, 9, 1);
        endDate = LocalDate.of(2026, 9, 30);
    }

    @Test
    void getReconciliation_whenValidDateRange_shouldReturnOk() {
        when(principal.getId()).thenReturn(userId);
        ReconciliationResponse serviceResponse = mock(ReconciliationResponse.class);
        when(reconciliationService.getReconciliation(userId, walletId, startDate, endDate)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<ReconciliationResponse>> response = reconciliationController.getReconciliation(principal, walletId, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reconciliationService).getReconciliation(userId, walletId, startDate, endDate);
    }

    @Test
    void getReconciliation_whenEndDateBeforeStartDate_shouldThrowAppException() {
        LocalDate invalidStartDate = LocalDate.of(2026, 9, 30);
        LocalDate invalidEndDate = LocalDate.of(2026, 9, 1);
        AppException exception = assertThrows(AppException.class, () -> reconciliationController.getReconciliation(principal, walletId, invalidStartDate, invalidEndDate));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ErrorMessage.Report.NOT_VALID_DATE, exception.getMessage());
        verifyNoInteractions(reconciliationService);
    }
}