package org.intern.personalfinancemanagementsystem.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.domain.entity.Category;

import java.util.UUID;

public record CategoryRequest(
        UUID parentId,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String name,
        @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
        String type
) {
}
