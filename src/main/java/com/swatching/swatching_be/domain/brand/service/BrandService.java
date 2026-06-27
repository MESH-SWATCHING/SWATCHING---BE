package com.swatching.swatching_be.domain.brand.service;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandImage;
import com.swatching.swatching_be.domain.brand.BrandKeyword;
import com.swatching.swatching_be.domain.brand.dto.BrandCardDto;
import com.swatching.swatching_be.domain.brand.dto.BrandDeckResponseDto;
import com.swatching.swatching_be.domain.brand.dto.BrandDetailResponse;
import com.swatching.swatching_be.domain.brand.dto.BrandRecommendResponse;
import com.swatching.swatching_be.domain.brand.dto.BrandResponseDto;
import com.swatching.swatching_be.domain.brand.repository.BrandImageRepository;
import com.swatching.swatching_be.domain.brand.repository.BrandKeywordRepository;
import com.swatching.swatching_be.domain.brand.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandKeywordRepository brandKeywordRepository;
    private final BrandImageRepository brandImageRepository;

    public List<BrandResponseDto> getBrands(String keywordsParam) {
        List<Brand> brands;

        if (keywordsParam == null || keywordsParam.isBlank() || keywordsParam.equalsIgnoreCase("all")) {
            brands = brandRepository.findAll();
        } else {
            List<String> keywordList = Arrays.asList(keywordsParam.split(","));
            brands = brandRepository.findBrandsHavingAnyKeyword(keywordList);
        }

        Map<Long, List<String>> keywordMap = brandKeywordRepository.findByBrandIn(brands).stream()
                .collect(Collectors.groupingBy(
                        bk -> bk.getBrand().getId(),
                        Collectors.mapping(bk -> bk.getKeyword().getName(), Collectors.toList())
                ));

        return brands.stream()
                .map(b -> new BrandResponseDto(b, keywordMap.getOrDefault(b.getId(), List.of())))
                .collect(Collectors.toList());
    }

    public BrandDeckResponseDto getBrandDeck(String keywordsParam) {
        List<Brand> brands;

        if (keywordsParam == null || keywordsParam.isBlank() || keywordsParam.equalsIgnoreCase("all")) {
            brands = brandRepository.findAll();
        } else {
            List<String> keywordList = Arrays.asList(keywordsParam.split(","));
            brands = brandRepository.findBrandsHavingAnyKeyword(keywordList);
        }

        Map<Long, List<String>> keywordMap = brandKeywordRepository.findByBrandIn(brands).stream()
                .collect(Collectors.groupingBy(
                        bk -> bk.getBrand().getId(),
                        Collectors.mapping(bk -> bk.getKeyword().getName(), Collectors.toList())
                ));

        Map<Long, List<String>> imageMap = brandImageRepository.findByBrandIn(brands).stream()
                .collect(Collectors.groupingBy(
                        bi -> bi.getBrand().getId(),
                        Collectors.mapping(BrandImage::getImageUrl, Collectors.toList())
                ));

        List<BrandCardDto> brandCards = brands.stream()
                .map(b -> new BrandCardDto(
                        b,
                        keywordMap.getOrDefault(b.getId(), List.of()),
                        imageMap.getOrDefault(b.getId(), List.of())
                ))
                .collect(Collectors.toList());

        return new BrandDeckResponseDto(brandCards);
    }

    public BrandDetailResponse getBrandDetail(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));

        List<String> visuals = brandImageRepository.findByBrand_Id(brandId)
                .stream()
                .map(BrandImage::getImageUrl)
                .toList();

        List<String> keywords = brandKeywordRepository.findByBrand_Id(brandId)
                .stream()
                .map(bk -> bk.getKeyword().getName())
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
