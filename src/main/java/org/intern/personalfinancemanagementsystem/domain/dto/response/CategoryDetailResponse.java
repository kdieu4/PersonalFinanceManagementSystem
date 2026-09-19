package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.Category;
import org.springframework.util.StringUtils;

public record CategoryDetailResponse(
        String parentName,
        String name,
        String type
) {
    public static CategoryDetailResponse from(Category category) {
        String parentName = category.getParent() != null ? category.getParent().getName() : "";
        return new CategoryDetailResponse(parentName, category.getName(), category.getType());
    }
}
