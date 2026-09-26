package org.intern.personalfinancemanagementsystem.controller;

import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.TransactionRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.TransactionDetailResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private CustomUserDetails principal;

    @Mock
    private TransactionRequest transactionRequest;

    private TransactionController transactionController;

    private UUID userId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        transactionController = new TransactionController(transactionService);

        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();

        when(principal.getId()).thenReturn(userId);
    }

    @Test
    void addTransaction_whenValidRequest_shouldReturnOk() {
        UUID createdTransactionId = UUID.randomUUID();

        when(transactionService.addTransaction(userId, transactionRequest)).thenReturn(createdTransactionId);

        ResponseEntity<ApiResponse<UUID>> response = transactionController.addTransaction(principal, transactionRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(transactionService).addTransaction(userId, transactionRequest);
    }

    @Test
    void getAllTransaction_whenValidRequest_shouldReturnOk() {
        int pageNo = 0;
        int pageSize = 20;

        PageResponse<List<TransactionDetailResponse>> serviceResponse = mock(PageResponse.class);

        when(transactionService.getAllTransaction(userId, pageNo, pageSize)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<PageResponse<List<TransactionDetailResponse>>>> response = transactionController.getAllCategoriesByUser(principal, pageNo, pageSize);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(transactionService).getAllTransaction(userId, pageNo, pageSize);
    }

    @Test
    void getTransactionDetail_whenTransactionExists_shouldReturnOk() {
        TransactionDetailResponse serviceResponse = mock(TransactionDetailResponse.class);

        when(transactionService.getTransactionDetail(userId, transactionId)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<TransactionDetailResponse>> response = transactionController.getAllCategoriesByUser(principal, transactionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(transactionService).getTransactionDetail(userId, transactionId);
    }

    @Test
    void exportTransaction_whenValidRequest_shouldReturnCsvFile() {
        byte[] csvData = """
                ID,Ngày giao dịch,Mục đích,Số tiền,Loại
                123,2026-09-25,Mua laptop,15000000.00,EXPENSE
                """.getBytes();

        when(transactionService.exportTransaction(userId)).thenReturn(csvData);

        ResponseEntity<byte[]> response = transactionController.exportTransaction(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertArrayEquals(csvData, response.getBody());

        assertEquals("attachment; filename = transactions.csv", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));

        assertEquals(MediaType.parseMediaType("text/csv"), response.getHeaders().getContentType());

        verify(transactionService).exportTransaction(userId);
    }
}