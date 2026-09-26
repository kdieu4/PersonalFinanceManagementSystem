package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.dto.request.TransactionRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.TransactionDetailResponse;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.intern.personalfinancemanagementsystem.domain.entity.Transaction;
import org.intern.personalfinancemanagementsystem.domain.entity.User;
import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;
import org.intern.personalfinancemanagementsystem.exception.AppException;
import org.intern.personalfinancemanagementsystem.repository.TransactionRepository;
import org.intern.personalfinancemanagementsystem.service.CategoryService;
import org.intern.personalfinancemanagementsystem.service.TransactionService;
import org.intern.personalfinancemanagementsystem.service.UserService;
import org.intern.personalfinancemanagementsystem.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {
    UserService userService;
    WalletService walletService;
    CategoryService categoryService;
    TransactionRepository transactionRepository;

    @Override
    @Transactional
    public UUID addTransaction(UUID userId, TransactionRequest request) {
        // 1. Lay reference cua wallet va category
        User user = userService.getReferenceById(userId);
        Wallet wallet = walletService.getReferenceById(request.walletId());
        Category category = categoryService.getReferenceById(request.categoryId());
        // 2. Cap nhat so du
        walletService.updateBalance(request.walletId(), request.amount(), request.type());
        // 3. Luu giao dich
        Transaction transaction = Transaction.builder()
                .user(user)
                .purpose(request.purpose())
                .wallet(wallet)
                .category(category)
                .amount(request.amount())
                .type(request.type())
                .transactionDate(request.transactionDate())
                .description(request.description())
                .build();
        transaction = transactionRepository.save(transaction);
        return transaction.getId();
    }

    @Override
    public PageResponse<List<TransactionDetailResponse>> getAllTransaction(UUID userId, int pageNo, int pageSize) {
        Page<Transaction> page = transactionRepository.findTransactionByUserId(userId, PageRequest.of(pageNo, pageSize));
        List<TransactionDetailResponse> list = page.stream().map(TransactionDetailResponse::from).toList();
        log.info("Lay toan bo danh sach giao dich voi tong " + page.getTotalPages());
        return new PageResponse<>(pageNo, pageSize, page.getTotalPages(), list);
    }

    @Override
    public TransactionDetailResponse getTransactionDetail(UUID userId, UUID transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorMessage.Transaction.TRANSACTION_NOT_EXISTED, ErrorMessage.NOT_FOUND_CODE));
        return TransactionDetailResponse.from(transaction);
    }

    @Override
    public byte[] exportTransaction(UUID userId) {
        User user = userService.getReferenceById(userId);
        List<Transaction> transactions = transactionRepository.findTransactionByUserId(userId);

        StringBuilder csv = new StringBuilder();

        csv.append("ID,Ngày giao dịch,Mục đích,Số tiền,Loại\n");

        for (Transaction transaction : transactions) {
            csv.append(transaction.getId()).append(",");
            csv.append(escapeCsv(transaction.getTransactionDate() != null ? transaction.getTransactionDate().toString() : "")).append(",");
            csv.append(escapeCsv(transaction.getPurpose())).append(",");
            csv.append(escapeCsv(transaction.getAmount().toString())).append(",");
            csv.append(escapeCsv(transaction.getType().toString())).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",")
                || value.contains("\"")
                || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
