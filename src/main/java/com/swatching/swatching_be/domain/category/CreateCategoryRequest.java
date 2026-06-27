package com.swatching.swatching_be.domain.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Size(max = 50, message = "카테고리 이름은 최대 50자까지 가능합니다.")
        String name
) {
}
