package org.intern.personalfinancemanagementsystem.controller;

import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @Mock
    private CustomUserDetails principal;

    @Mock
    private WalletRequest walletRequest;

    private WalletController walletController;

    private UUID userId;
    private UUID walletId;

    @BeforeEach
    void setUp() {
        walletController = new WalletController(walletService);

        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();

        when(principal.getId()).thenReturn(userId);
    }

    @Test
    void getAllWallet_whenValidRequest_shouldReturnOk() {
        int pageNo = 0;
        int pageSize = 20;

        PageResponse<List<WalletDetailResponse>> serviceResponse = mock(PageResponse.class);

        when(walletService.getAllWallets(userId, pageNo, pageSize)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<PageResponse<List<WalletDetailResponse>>>> response = walletController.getAllWallet(principal, pageNo, pageSize);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(walletService).getAllWallets(userId, pageNo, pageSize);
    }

    @Test
    void addWallet_whenValidRequest_shouldReturnOk() {
        UUID createdWalletId = UUID.randomUUID();

        when(walletService.addWallet(userId, walletRequest)).thenReturn(createdWalletId);

        ResponseEntity<ApiResponse<UUID>> response = walletController.addWallet(principal, walletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(walletService).addWallet(userId, walletRequest);
    }

    @Test
    void updateWallet_whenValidRequest_shouldReturnOk() {
        ResponseEntity<ApiResponse<UUID>> response = walletController.updateWallet(principal, walletId, walletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(walletService).updateWallet(userId, walletId, walletRequest);
    }

    @Test
    void deleteWallet_whenWalletExists_shouldReturnOk() {
        ResponseEntity<ApiResponse<Void>> response = walletController.deleteWallet(principal, walletId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(walletService).deleteWallet(userId, walletId);
    }

    @Test
    void getWalletDetail_whenWalletExists_shouldReturnOk() {
        WalletDetailResponse serviceResponse = mock(WalletDetailResponse.class);

        when(walletService.getWalletDetail(userId, walletId)).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<WalletDetailResponse>> response = walletController.getWalletDetail(principal, walletId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(walletService).getWalletDetail(userId, walletId);
    }
}
