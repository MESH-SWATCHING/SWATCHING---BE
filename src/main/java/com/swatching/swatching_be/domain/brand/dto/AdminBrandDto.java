package com.swatching.swatching_be.domain.brand.dto;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandVisibility;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AdminBrandDto {

    @Getter
    @Builder
    public static class Response {
        private Long brandId;
        private String name;
        private String summary;
        private String mainImageUrl;
        private BrandVisibility status;
        private String rejectReason;
        private LocalDateTime createdAt;

        public static Response from(Brand brand) {
            return Response.builder()
                    .brandId(brand.getId())
                    .name(brand.getName())
                    .summary(brand.getSummary())
                    .mainImageUrl(brand.getMainImageUrl())
                    .status(brand.getVisibility())
                    .rejectReason(brand.getRejectReason())
                    .createdAt(brand.getCreatedAt())
                    .build();
        }
    }

    @Getter
    public static class RejectRequest {
        private String reason;
    }
}
