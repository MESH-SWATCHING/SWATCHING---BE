package com.swatching.swatching_be.domain.brand.dto;

import com.swatching.swatching_be.domain.brand.Brand;
import lombok.Getter;

import java.util.List;

@Getter
public class BrandCardDto {

    private final Long brandId;
    private final String name;
    private final String summary;
    private final String storySummary;
    private final String mainImageUrl;
    private final String instagramUrl;
    private final String websiteUrl;
    private final List<String> keywords;
    private final List<String> visualPreviews;

    public BrandCardDto(Brand brand, List<String> keywords, List<String> visualPreviews) {
        this.brandId = brand.getId();
        this.name = brand.getName();
        this.summary = brand.getSummary();
        this.storySummary = brand.getStorySummary();
        this.mainImageUrl = brand.getMainImageUrl();
        this.instagramUrl = brand.getInstagramUrl();
        this.websiteUrl = brand.getWebsiteUrl();
        this.keywords = keywords;
        this.visualPreviews = visualPreviews;
    }
}
