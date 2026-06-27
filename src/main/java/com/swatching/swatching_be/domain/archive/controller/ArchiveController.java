package com.swatching.swatching_be.domain.archive.controller;

import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.archive.service.ArchiveService;
import com.swatching.swatching_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
