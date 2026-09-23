package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.TransactionRequest;

import java.util.UUID;


public interface TransactionService {
    UUID addTransaction(TransactionRequest request);
}
