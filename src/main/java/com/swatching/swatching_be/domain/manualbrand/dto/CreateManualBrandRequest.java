package com.swatching.swatching_be.domain.manualbrand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateManualBrandRequest(
        @NotBlank(message = "브랜드 이름은 필수입니다.")
        @Size(max = 255, message = "브랜드 이름은 최대 255자까지 가능합니다.")
        String name,
        @Size(max = 2048, message = "instagramUrl은 너무 깁니다.")
        String instagramUrl,
        @Size(max = 2048, message = "websiteUrl은 너무 깁니다.")
        String websiteUrl,
        @Size(max = 500, message = "메모는 최대 500자까지 가능합니다.")
        String memo,
        List<Long> categoryIds
) {
}
