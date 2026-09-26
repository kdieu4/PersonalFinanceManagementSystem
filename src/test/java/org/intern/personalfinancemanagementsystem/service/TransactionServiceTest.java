package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.TransactionRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.TransactionDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.*;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.TransactionRepository;
import org.intern.personalfinancemanagementsystem.service.impl.CategoryServiceImpl;
import org.intern.personalfinancemanagementsystem.service.impl.TransactionServiceImpl;
import org.intern.personalfinancemanagementsystem.service.impl.UserServiceImpl;
import org.intern.personalfinancemanagementsystem.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    UserServiceImpl userService;

    @Mock
    WalletServiceImpl walletService;

    @Mock
    CategoryServiceImpl categoryService;

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionServiceImpl transactionService;

    private UUID userId;
    private UUID walletId;
    private UUID categoryId;
    private UUID transactionId;

    private User mockUser;
    private Wallet mockWallet;
    private Category mockCategory;
    private Transaction mockTransaction;

    TransactionRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        transactionId = UUID.randomUUID();

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail("test@gmail.com");

        mockWallet = new Wallet();
        mockWallet.setId(walletId);
        mockWallet.setName("MOMO");
        mockWallet.setBalance(new BigDecimal("1000000"));
        mockWallet.setCurrency("VND");

        mockCategory = new Category();
        mockCategory.setId(categoryId);
        mockCategory.setName("Ăn uống");

        mockTransaction = Transaction.builder()
                .id(transactionId)
                .user(mockUser)
                .wallet(mockWallet)
                .category(mockCategory)
                .purpose("Ăn trưa")
                .amount(new BigDecimal("50000"))
                .type(TransactionType.EXPENSE)
                .transactionDate(LocalDate.of(2026, 9, 25))
                .description("Ăn trưa với bạn")
                .build();

        request = new TransactionRequest("Ăn trưa",
                new BigDecimal("50000"),
                TransactionType.EXPENSE,
                "Ăn trưa với bạn",
                LocalDate.of(2026, 9, 25),
                walletId,
                categoryId
        );
    }

    @Test
    void addTransaction_whenValidRequest_shouldReturnTransactionId() {
        when(userService.getReferenceById(userId)).thenReturn(mockUser);
        when(walletService.getReferenceById(walletId)).thenReturn(mockWallet);
        when(categoryService.getReferenceById(categoryId)).thenReturn(mockCategory);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        UUID response = transactionService.addTransaction(userId, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(transactionId, response);
    }

    @Test
    void getAllTransaction_whenValidRequest_ShouldReturnSuccess() {
        int pageNo = 0;
        int pageSize = 10;

        Page<Transaction> page = new PageImpl<>(List.of(mockTransaction), PageRequest.of(pageNo, pageSize), 1);

        when(transactionRepository.findTransactionByUserId(userId, PageRequest.of(pageNo, pageSize))).thenReturn(page);

        PageResponse<List<TransactionDetailResponse>> response = transactionService.getAllTransaction(userId, pageNo, pageSize);

        Assertions.assertNotNull(response);
        Mockito.verify(transactionRepository).findTransactionByUserId(userId, PageRequest.of(pageNo, pageSize));
    }

    @Test
    void getTransactionDetail_whenValidRequest_ShouldReturnSuccess() {
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.of(mockTransaction));

        TransactionDetailResponse response = transactionService.getTransactionDetail(userId, transactionId);

        Assertions.assertNotNull(response);

        Mockito.verify(transactionRepository).findByIdAndUserId(transactionId, userId);
    }

    @Test
    void getTransactionDetail_whenIdNotFound_ShouldThrowException() {
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        AppException caughtException = Assertions.assertThrows(AppException.class,
                () -> transactionService.getTransactionDetail(userId, transactionId));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, caughtException.getStatus());
    }

    @Test
    void exportTransaction_whenValidRequest_shouldReturnCsv() {
        when(userService.getReferenceById(userId)).thenReturn(mockUser);
        when(transactionRepository.findTransactionByUserId(userId)).thenReturn(List.of(mockTransaction));
        byte[] response = transactionService.exportTransaction(userId);
        Assertions.assertNotNull(response);
    }

    @Test
    void exportTransaction_whenPurposeContainsComma_shouldEscapeCsv() {
        mockTransaction.setPurpose("Ăn trưa, uống nước");
        when(userService.getReferenceById(userId)).thenReturn(mockUser);
        when(transactionRepository.findTransactionByUserId(userId)).thenReturn(List.of(mockTransaction));

        byte[] response = transactionService.exportTransaction(userId);
        String csv = new String(response, StandardCharsets.UTF_8);

        Assertions.assertTrue(csv.contains("\"Ăn trưa, uống nước\""));
    }

    @Test
    void exportTransaction_WhenPurposeContainsQuote_ShouldEscapeCsv() {
        mockTransaction.setPurpose("Mua \"Laptop\"");

        when(userService.getReferenceById(userId)).thenReturn(mockUser);
        when(transactionRepository.findTransactionByUserId(userId)).thenReturn(List.of(mockTransaction));

        byte[] response = transactionService.exportTransaction(userId);

        String csv = new String(response, StandardCharsets.UTF_8);

        Assertions.assertTrue(csv.contains("\"Mua \"\"Laptop\"\"\""));
    }

    @Test
    void exportTransaction_WhenTransactionDateIsNull_ShouldReturnEmptyDate() {
        mockTransaction.setTransactionDate(null);

        when(userService.getReferenceById(userId)).thenReturn(mockUser);
        when(transactionRepository.findTransactionByUserId(userId)).thenReturn(List.of(mockTransaction));

        byte[] response = transactionService.exportTransaction(userId);

        String csv = new String(response, StandardCharsets.UTF_8);

        Assertions.assertTrue(csv.contains(transactionId + ",,Ăn trưa"));
    }
}