package com.swatching.swatching_be.domain.brand.service;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandKeyword;
import com.swatching.swatching_be.domain.brand.dto.BrandResponseDto;
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
}
