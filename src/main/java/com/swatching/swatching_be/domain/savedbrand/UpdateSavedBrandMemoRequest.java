package com.swatching.swatching_be.domain.savedbrand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSavedBrandMemoRequest(
        @NotBlank(message = "메모는 필수입니다.")
        @Size(max = 500, message = "메모는 최대 500자까지 가능합니다.")
        String memo
) {
}
