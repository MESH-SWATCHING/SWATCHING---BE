package com.swatching.swatching_be.brand.controller;

import com.swatching.swatching_be.brand.dto.BrandDetailResponse;
import com.swatching.swatching_be.brand.dto.BrandRecommendResponse;
import com.swatching.swatching_be.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping("/{brandId}")
    public BrandDetailResponse getBrandDetail(@PathVariable Long brandId) {
        return brandService.getBrandDetail(brandId);
    }

    @GetMapping("/{brandId}/recommend")
    public List<BrandRecommendResponse> getRecommendedBrands(@PathVariable Long brandId) {
        return brandService.getRecommendedBrands(brandId);
    }
}