package com.swatching.swatching_be.domain.image.dto;

public record ImageUploadResponse(
        String imageUrl,
        String objectKey,
        String contentType,
        long size
) {
}
