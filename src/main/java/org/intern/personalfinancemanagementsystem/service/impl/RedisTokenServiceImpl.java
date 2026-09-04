package org.intern.personalfinancemanagementsystem.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.domain.entity.RedisToken;
import org.intern.personalfinancemanagementsystem.repository.RedisTokenRepository;
import org.intern.personalfinancemanagementsystem.service.RedisTokenService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisTokenServiceImpl implements RedisTokenService {
    RedisTokenRepository redisTokenRepository;

    @Override
    public void save(RedisToken token) {
        redisTokenRepository.save(token);
    }
}
