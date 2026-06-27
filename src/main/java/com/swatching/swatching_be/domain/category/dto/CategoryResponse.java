package com.swatching.swatching_be.domain.category.dto;

import com.swatching.swatching_be.domain.category.Category;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long categoryId,
        String name,
        boolean isDefault,
        LocalDateTime createdAt
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isDefault(),
                category.getCreatedAt()
        );
    }
}
