package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.Category;

import java.util.List;
import java.util.UUID;

public record CategoryDetailResponse(
        UUID id,
        UUID parentId,
        String parentName,
        String name,
        String type,
        List<CategoryDetailResponse> children
) {
    public static CategoryDetailResponse from(Category category) {
        return from(category, null);
    }

    public static CategoryDetailResponse from(Category category, List<CategoryDetailResponse> children) {
        String parentName;
        UUID parentId;
        if (category.getParent() != null) {
            parentName = category.getParent().getName();
            parentId = category.getParent().getId();
        } else {
            parentName = "";
            parentId = null;
        }
        return new CategoryDetailResponse(category.getId(), parentId, parentName, category.getName(), category.getType(), children);
    }
}
