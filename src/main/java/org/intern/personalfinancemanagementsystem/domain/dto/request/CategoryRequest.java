package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;

public record CategoryRequest(
        String parentName,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String name,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String type
) {
    public static CategoryRequest from(Category category) {
        return new CategoryRequest(category.getParent().getName(), category.getName(), category.getType());
    }
}
