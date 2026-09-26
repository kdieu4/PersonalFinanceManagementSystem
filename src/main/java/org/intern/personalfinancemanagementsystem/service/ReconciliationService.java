package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.response.ReconciliationResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface ReconciliationService {
    ReconciliationResponse getReconciliation(UUID userId, UUID walletId, LocalDate startDate, LocalDate endDate);
}