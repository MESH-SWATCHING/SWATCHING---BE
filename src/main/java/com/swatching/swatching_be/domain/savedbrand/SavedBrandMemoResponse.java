package com.swatching.swatching_be.domain.savedbrand;

import java.time.LocalDateTime;

public record SavedBrandMemoResponse(
        Long savedBrandId,
        String memo,
        LocalDateTime updatedAt
) {

    public static SavedBrandMemoResponse from(SavedBrand savedBrand) {
        return new SavedBrandMemoResponse(savedBrand.getId(), savedBrand.getMemo(), savedBrand.getUpdatedAt());
    }
}
