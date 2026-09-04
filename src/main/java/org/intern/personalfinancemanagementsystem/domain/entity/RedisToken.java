package org.intern.personalfinancemanagementsystem.domain.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("RedisToken")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisToken {
    private String id;
    private String accessToken;
    private String refreshToken;
    private String resetToken;
}