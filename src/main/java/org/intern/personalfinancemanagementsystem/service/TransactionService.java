package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.TransactionRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.TransactionDetailResponse;

import java.util.List;
import java.util.UUID;


public interface TransactionService {
    UUID addTransaction(UUID userId, TransactionRequest request);

    PageResponse<List<TransactionDetailResponse>> getAllTransaction(UUID userId, int pageNo, int pageSize);

    TransactionDetailResponse getTransactionDetail(UUID userId, UUID transactionId);

    byte[] exportTransaction(UUID userId);
}
