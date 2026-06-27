package com.swatching.swatching_be.domain.brand.controller;

import com.swatching.swatching_be.domain.brand.dto.BrandDeckResponseDto;
import com.swatching.swatching_be.domain.brand.dto.BrandResponseDto;
import com.swatching.swatching_be.domain.brand.service.BrandService;
import com.swatching.swatching_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandResponseDto>>> getBrands(
            @RequestParam(required = false) String keywords) {
        List<BrandResponseDto> data = brandService.getBrands(keywords);
        return ResponseEntity.ok(ApiResponse.success("필터 결과에 따른 신규 브랜드 목록 조회가 완료되었습니다.", data));
    }

    @GetMapping("/brands/deck")
    public ResponseEntity<ApiResponse<BrandDeckResponseDto>> getBrandDeck(
            @RequestParam(required = false) String keywords) {
        BrandDeckResponseDto data = brandService.getBrandDeck(keywords);
        return ResponseEntity.ok(ApiResponse.success("덱 탐색용 브랜드 카드 목록 조회가 완료되었습니다.", data));
    }
}
