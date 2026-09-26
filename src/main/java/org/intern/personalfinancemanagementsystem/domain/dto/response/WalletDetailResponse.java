package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletDetailResponse(
        UUID id,
        String name,
        BigDecimal balance,
        String currency
) {
    public static WalletDetailResponse from(Wallet wallet) {
        return new WalletDetailResponse(wallet.getId(), wallet.getName(), wallet.getBalance(), wallet.getCurrency());
    }
}
