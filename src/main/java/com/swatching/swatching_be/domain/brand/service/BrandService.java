package com.swatching.swatching_be.domain.brand.service;

import com.swatching.swatching_be.domain.brand.dto.BrandDetailResponse;
import com.swatching.swatching_be.domain.brand.dto.BrandRecommendResponse;
import com.swatching.swatching_be.domain.brand.entity.Brand;
import com.swatching.swatching_be.domain.brand.entity.BrandImage;
import com.swatching.swatching_be.domain.brand.repository.BrandImageRepository;
import com.swatching.swatching_be.domain.brand.repository.BrandKeywordRepository;
import com.swatching.swatching_be.domain.brand.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandImageRepository brandImageRepository;
    private final BrandKeywordRepository brandKeywordRepository;

    public BrandDetailResponse getBrandDetail(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));

        List<String> visuals = brandImageRepository.findByBrand_IdOrderBySortOrderAsc(brandId)
                .stream()
                .map(BrandImage::getImageUrl)
                .toList();

        List<String> keywords = brandKeywordRepository.findByBrand_Id(brandId)
                .stream()
                .map(brandKeyword -> brandKeyword.getKeyword().getName())
                .toList();

        return new BrandDetailResponse(
                brand.getId(),
                brand.getName(),
                brand.getSummary(),
                brand.getStory(),
                brand.getStorySummary(),
                brand.getMainImageUrl(),
                brand.getInstagramUrl(),
                brand.getWebsiteUrl(),
                keywords,
                visuals
        );
    }

    public List<BrandRecommendResponse> getRecommendedBrands(Long brandId) {
        return List.of();
    }
}