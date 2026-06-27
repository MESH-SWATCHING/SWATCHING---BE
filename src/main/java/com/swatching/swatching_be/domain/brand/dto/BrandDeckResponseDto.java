package com.swatching.swatching_be.domain.brand.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BrandDeckResponseDto {

    private final int totalCount;
    private final List<BrandCardDto> brandCards;

    public BrandDeckResponseDto(List<BrandCardDto> brandCards) {
        this.totalCount = brandCards.size();
        this.brandCards = brandCards;
    }
}
