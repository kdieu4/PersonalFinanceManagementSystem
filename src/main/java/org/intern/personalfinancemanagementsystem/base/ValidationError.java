package org.intern.personalfinancemanagementsystem.base;

public record ValidationError(
        String field,
        String issue
) {
}
