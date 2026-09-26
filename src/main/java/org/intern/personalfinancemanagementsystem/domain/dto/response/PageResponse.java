package org.intern.personalfinancemanagementsystem.domain.dto.response;

public record PageResponse<T>(
        int pageNo,
        int pageSize,
        int totalPage,
        T items
) {
}
