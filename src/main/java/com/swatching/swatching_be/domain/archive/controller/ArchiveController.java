package com.swatching.swatching_be.domain.archive.controller;

import com.swatching.swatching_be.domain.archive.dto.ArchiveReqDTO;
import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.archive.service.ArchiveService;
import com.swatching.swatching_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ArchiveController {

    private final ArchiveService archiveService;

    //my swatch 카테고리 목록 조회
    @GetMapping("/my-swatch/categories")
    public ApiResponse<ArchiveResDTO.CategoryListDTO> getMySwatchCategories() {
        Long userId = 1L; // TODO: 로그인 구현 후 인증 객체에서 userId 가져오기
        return ApiResponse.success("카테고리 목록 조회 성공", archiveService.getMySwatchCategories(userId));
    }

    //브랜드 저장하기
    @PostMapping("/brands/{brandId}/save")
    public ApiResponse<Void> saveBrand(@PathVariable Long brandId,
                                       @RequestBody ArchiveReqDTO.SaveBrandDTO request) {
        Long userId = 1L; // TODO: 로그인 구현 후 인증 객체에서 userId 가져오기
        archiveService.saveBrand(userId, brandId, request);
        return ApiResponse.success("브랜드 저장 성공", null);
    }
}
