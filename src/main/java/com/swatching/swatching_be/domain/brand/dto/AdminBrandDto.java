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
        private String submitterNickname;
        private String managerName;
        private String managerEmail;
        private String managerPhone;
        private String instagramUrl;
        private String websiteUrl;

        public static Response from(Brand brand) {
            return Response.builder()
                    .brandId(brand.getId())
                    .name(brand.getName())
                    .summary(brand.getSummary())
                    .mainImageUrl(brand.getMainImageUrl())
                    .status(brand.getVisibility())
                    .rejectReason(brand.getRejectReason())
                    .createdAt(brand.getCreatedAt())
                    .submitterNickname(brand.getOwnerUser() != null ? brand.getOwnerUser().getNickname() : null)
                    .managerName(brand.getManagerName())
                    .managerEmail(brand.getManagerEmail())
                    .managerPhone(brand.getManagerPhone())
                    .instagramUrl(brand.getInstagramUrl())
                    .websiteUrl(brand.getWebsiteUrl())
                    .build();
        }
    }

    @Getter
    public static class RejectRequest {
        private String reason;
    }
}
