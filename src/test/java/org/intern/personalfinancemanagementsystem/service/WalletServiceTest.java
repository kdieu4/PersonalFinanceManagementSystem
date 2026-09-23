package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.CategoryRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.request.WalletRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.CategoryDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.WalletDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.WalletRepository;
import org.intern.personalfinancemanagementsystem.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {
    @Mock
    WalletRepository walletRepository;
    @Mock
    UserService userService;
    @Mock
    Page<Wallet> getAllWallet;
    @InjectMocks
    WalletServiceImpl walletService;

    @Test
    void getAllWallets_WhenValidRequest_ShouldReturnSuccess() {
        // 1. Arrange
        String testEmail = "test@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);
        int testPageNo = 0;
        int testPageSize = 20;

        when(walletRepository.findWalletByUserId(mockUser.getId(), PageRequest.of(testPageNo, testPageSize))).thenReturn(getAllWallet);
        // 2. Act
        PageResponse<List<WalletDetailResponse>> response = walletService.getAllWallets(mockUser.getId(), testPageNo, testPageSize);
        // 3. Assert
        Assertions.assertNotNull(response);
    }

    @Test
    void addWallet_WhenValidRequest_ShouldReturnSuccess() {
        // 1. Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(id);
        id = UUID.randomUUID();
        Wallet mockWallet = new Wallet();
        mockWallet.setId(id);
        String name = "MOMO";
        BigDecimal balance = new BigDecimal(50000);
        String currency = "VND";

        WalletRequest request = new WalletRequest(name, balance, currency);

        when(userService.getReferenceById(mockUser.getId())).thenReturn(mockUser);
        when(walletRepository.save(ArgumentMatchers.any(Wallet.class))).thenReturn(mockWallet);
        // 2. Act
        UUID response = walletService.addWallet(mockUser.getId(), request);
        // 3. Assert
        Assertions.assertNotNull(response);
    }

    @Test
    void addWallet_WhenWalletExisted_ShouldThrowException() {
        // 1. Arrange
        UUID id = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(id);
        id = UUID.randomUUID();
        Wallet mockWallet = new Wallet();
        mockWallet.setId(id);
        String name = "MOMO";
        BigDecimal balance = new BigDecimal(50000);
        String currency = "VND";
        WalletRequest request = new WalletRequest(name, balance, currency);

        when(userService.getReferenceById(mockUser.getId())).thenReturn(mockUser);
        when(walletRepository.existsByNameAndUserId(request.name(), mockUser.getId())).thenReturn(true);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            walletService.addWallet(mockUser.getId(), request);
        });

        Assertions.assertEquals(ErrorMessage.Wallet.WALLET_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void updateWallet_WhenValidRequest_ShouldReturnTrue() {
        UUID testUserId = UUID.randomUUID();
        UUID testWalletId = UUID.randomUUID();

        Wallet mockWallet = new Wallet();
        mockWallet.setId(testWalletId);

        String updateName = "VietinBank";
        BigDecimal updateBalance = new BigDecimal(50000);
        String updateCurrency = "VND";

        WalletRequest request = new WalletRequest(updateName, updateBalance, updateCurrency);

        when(walletRepository.findByIdAndUserId(testWalletId, testUserId)).thenReturn(Optional.of(mockWallet));
        when(walletRepository.existsByNameAndUserIdAndIdNot(request.name(), testUserId, testWalletId)).thenReturn(false);

        walletService.updateWallet(testUserId, testWalletId, request);

        Assertions.assertEquals(updateName, mockWallet.getName());
        Assertions.assertEquals(updateBalance, mockWallet.getBalance());
        Assertions.assertEquals(updateCurrency, mockWallet.getCurrency());
    }

    @Test
    void updateWallet_WhenWalletExisted_ShouldThrowException() {
        UUID testUserId = UUID.randomUUID();
        UUID testWalletId = UUID.randomUUID();

        Wallet mockWallet = new Wallet();
        mockWallet.setId(testWalletId);

        String updateName = "VietinBank";
        BigDecimal updateBalance = new BigDecimal(50000);
        String updateCurrency = "VND";

        WalletRequest request = new WalletRequest(updateName, updateBalance, updateCurrency);

        when(walletRepository.existsByNameAndUserIdAndIdNot(request.name(), testUserId, testWalletId)).thenReturn(true);

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            walletService.updateWallet(testUserId, testWalletId, request);
        });

        Assertions.assertEquals(ErrorMessage.Wallet.WALLET_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void updateWallet_WhenIdNotFound_ShouldThrowException() {
        UUID testUserId = UUID.randomUUID();
        UUID testWalletId = UUID.randomUUID();

        Wallet mockWallet = new Wallet();
        mockWallet.setId(testWalletId);

        String updateName = "VietinBank";
        BigDecimal updateBalance = new BigDecimal(50000);
        String updateCurrency = "VND";

        WalletRequest request = new WalletRequest(updateName, updateBalance, updateCurrency);

        when(walletRepository.existsByNameAndUserIdAndIdNot(request.name(), testUserId, testWalletId)).thenReturn(false);
        when(walletRepository.findByIdAndUserId(testWalletId, testUserId)).thenReturn(Optional.empty());

        AppException caughtException = Assertions.assertThrows(AppException.class, () -> {
            walletService.updateWallet(testUserId, testWalletId, request);
        });

        Assertions.assertEquals(ErrorMessage.Wallet.WALLET_NOT_EXISTED, caughtException.getErrorMessage());
    }

    @Test
    void deleteWallet_WhenValidRequest_ShouldReturnTrue() {
        UUID testUserId = UUID.randomUUID();
        UUID testWalletId = UUID.randomUUID();
        walletService.deleteWallet(testUserId, testWalletId);
        Mockito.verify(walletRepository, Mockito.times(1)).softDeleteWallet(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(Instant.class), ArgumentMatchers.any(UUID.class));
    }

    @Test
    void getWalletDetail_WhenValidRequest_ShouldReturnTrue() {
        UUID testUserId = UUID.randomUUID();
        UUID testCategoryId = UUID.randomUUID();
        Wallet mockCategory = new Wallet();
        mockCategory.setId(testCategoryId);
        when(walletRepository.findByIdAndUserId(testCategoryId, testUserId)).thenReturn(Optional.of(mockCategory));
        WalletDetailResponse response = walletService.getWalletDetail(testUserId, testCategoryId);
        Assertions.assertNotNull(response);
    }
}
