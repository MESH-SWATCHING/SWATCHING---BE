package com.swatching.swatching_be.domain.archive.controller;

import com.swatching.swatching_be.domain.archive.dto.ArchiveReqDTO;
import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.archive.service.ArchiveService;
import com.swatching.swatching_be.global.auth.CurrentUserProvider;
import com.swatching.swatching_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ArchiveController {

    private final ArchiveService archiveService;
    private final CurrentUserProvider currentUserProvider;

    //my swatch 카테고리 목록 조회
    @GetMapping("/my-swatch/categories")
    public ApiResponse<ArchiveResDTO.CategoryListDTO> getMySwatchCategories() {
        Long userId = currentUserProvider.getCurrentUser().getId();
        return ApiResponse.success("카테고리 목록 조회 성공", archiveService.getMySwatchCategories(userId));
    }

    //브랜드 저장하기
    @PostMapping("/brands/{brandId}/save")
    public ApiResponse<Void> saveBrand(@PathVariable Long brandId,
                                       @RequestBody ArchiveReqDTO.SaveBrandDTO request) {
        Long userId = currentUserProvider.getCurrentUser().getId();
        archiveService.saveBrand(userId, brandId, request);
        return ApiResponse.success("브랜드 저장 성공", null);
    }

    //카테고리별 저장 브랜드 조회
    @GetMapping("/my-swatch/categories/{categoryId}/brands")
    public ApiResponse<ArchiveResDTO.SavedBrandListDTO> getSavedBrandsByCategory(@PathVariable Long categoryId) {
        Long userId = currentUserProvider.getCurrentUser().getId();
        return ApiResponse.success("카테고리별 브랜드 조회 성공", archiveService.getSavedBrandsByCategory(userId, categoryId));
    }
}
