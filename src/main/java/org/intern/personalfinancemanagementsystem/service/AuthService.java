package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.dto.request.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
}
