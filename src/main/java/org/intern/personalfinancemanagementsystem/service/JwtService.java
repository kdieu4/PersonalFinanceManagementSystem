package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.entity.User;

public interface JwtService {
    String generateToken(User user);
}
