package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.domain.entity.RedisToken;

public interface RedisTokenService {
    void save (RedisToken token);
    void remove (String id);
}
