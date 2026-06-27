package com.swatching.swatching_be.domain.manualbrand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateManualBrandRequest(
        @NotBlank(message = "Brand name is required.")
        @Size(max = 255, message = "Brand name must be 255 characters or less.")
        String name,
        @Size(max = 2048, message = "instagramUrl must be 2048 characters or less.")
        String instagramUrl,
        @Size(max = 2048, message = "websiteUrl must be 2048 characters or less.")
        String websiteUrl,
        @Size(max = 500, message = "Memo must be 500 characters or less.")
        String memo,
        List<Long> categoryIds
) {
}
