package org.intern.personalfinancemanagementsystem.domain.dto.response;

import org.intern.personalfinancemanagementsystem.domain.entity.Category;

import java.util.UUID;

public record CategoryDetailResponse(
        UUID id,
        UUID parentId,
        String parentName,
        String name,
        String type
) {
    public static CategoryDetailResponse from(Category category) {
        String parentName;
        UUID parentId;
        if (category.getParent() != null) {
            parentName = category.getParent().getName();
            parentId = category.getId();
        } else {
            parentName = "";
            parentId = null;
        }
        return new CategoryDetailResponse(category.getId(), parentId, parentName, category.getName(), category.getType());
    }
}
