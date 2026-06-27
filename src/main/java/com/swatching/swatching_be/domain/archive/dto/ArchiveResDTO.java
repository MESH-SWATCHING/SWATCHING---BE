package com.swatching.swatching_be.domain.archive.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ArchiveResDTO {
    @Getter
    @Builder
    public static class CategoryListDTO {
        private Long totalSavedBrandCount;
        private List<CategoryDTO> categories;
    }

    @Getter
    @Builder
    public static class CategoryDTO {
        private Long categoryId;
        private String name;
        private Boolean isDefault;
        private Long brandCount;
    }
}
