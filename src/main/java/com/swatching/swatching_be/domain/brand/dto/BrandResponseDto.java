package com.swatching.swatching_be.domain.brand.dto;

import com.swatching.swatching_be.domain.brand.Brand;
import lombok.Getter;

import java.util.List;

@Getter
public class BrandResponseDto {

    private final Long brandId;
    private final String name;
    private final String summary;
    private final String mainImageUrl;
    private final List<String> keywords;

    public BrandResponseDto(Brand brand, List<String> keywords) {
        this(brand, keywords, brand.getMainImageUrl());
    }

    public BrandResponseDto(Brand brand, List<String> keywords, String mainImageUrl) {
        this.brandId = brand.getId();
        this.name = brand.getName();
        this.summary = brand.getSummary();
        this.mainImageUrl = mainImageUrl;
        this.keywords = keywords;
    }
}
