package com.swatching.swatching_be.domain.archive.converter;

import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.archive.entity.UserCategory;

import java.util.List;
import java.util.Map;

public class ArchiveConverter {
    public static ArchiveResDTO.CategoryListDTO toCategoryListDTO(
            Long totalSavedBrandCount,
            List<UserCategory> categories,
            Map<Long, Long> categoryBrandCountMap
    ) {
        List<ArchiveResDTO.CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> toCategoryDTO(category, categoryBrandCountMap))
                .toList();

        return ArchiveResDTO.CategoryListDTO.builder()
                .totalSavedBrandCount(totalSavedBrandCount)
                .categories(categoryDTOs)
                .build();
    }

    private static ArchiveResDTO.CategoryDTO toCategoryDTO(
            UserCategory category,
            Map<Long, Long> categoryBrandCountMap
    ) {
        return ArchiveResDTO.CategoryDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .isDefault(category.getIsDefault())
                .brandCount(categoryBrandCountMap.getOrDefault(category.getId(), 0L))
                .build();
    }
}
