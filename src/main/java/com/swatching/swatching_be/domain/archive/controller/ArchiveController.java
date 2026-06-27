package com.swatching.swatching_be.domain.archive.controller;

import com.swatching.swatching_be.domain.archive.dto.ArchiveReqDTO;
import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.archive.service.ArchiveService;
import com.swatching.swatching_be.global.auth.CurrentUserProvider;
import com.swatching.swatching_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ArchiveController {

    private final ArchiveService archiveService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/my-swatch/categories")
    public ApiResponse<ArchiveResDTO.CategoryListDTO> getMySwatchCategories() {
        Long userId = currentUserProvider.getCurrentUser().getId();
        return ApiResponse.success("Categories loaded.", archiveService.getMySwatchCategories(userId));
    }

    @PostMapping("/brands/{brandId}/save")
    public ApiResponse<Void> saveBrand(
            @PathVariable Long brandId,
            @RequestBody ArchiveReqDTO.SaveBrandDTO request
    ) {
        Long userId = currentUserProvider.getCurrentUser().getId();
        archiveService.saveBrand(userId, brandId, request);
        return ApiResponse.success("Brand saved.", null);
    }

    @PostMapping("/my-swatch/categories/{categoryId}/saved-brands")
    public ApiResponse<Void> addSavedBrandsToCategory(
            @PathVariable Long categoryId,
            @RequestBody ArchiveReqDTO.AddSavedBrandsToCategoryDTO request
    ) {
        Long userId = currentUserProvider.getCurrentUser().getId();
        archiveService.addSavedBrandsToCategory(userId, categoryId, request);
        return ApiResponse.success("Brands added to category.", null);
    }

    @GetMapping("/my-swatch/categories/{categoryId}/brands")
    public ApiResponse<ArchiveResDTO.SavedBrandListDTO> getSavedBrandsByCategory(@PathVariable Long categoryId) {
        Long userId = currentUserProvider.getCurrentUser().getId();
        return ApiResponse.success("Category brands loaded.", archiveService.getSavedBrandsByCategory(userId, categoryId));
    }
}
