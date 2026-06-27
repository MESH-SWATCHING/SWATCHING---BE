package com.swatching.swatching_be.domain.archive.converter;

import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.category.Category;

import java.util.List;
import java.util.Map;

public class ArchiveConverter {
    public static ArchiveResDTO.CategoryListDTO toCategoryListDTO(
            Long totalSavedBrandCount,
            List<Category> categories,
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
            Category category,
            Map<Long, Long> categoryBrandCountMap
    ) {
        return ArchiveResDTO.CategoryDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .isDefault(category.isDefault())
                .brandCount(categoryBrandCountMap.getOrDefault(category.getId(), 0L))
                .build();
    }
}
