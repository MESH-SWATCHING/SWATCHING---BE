package com.swatching.swatching_be.domain.brand.controller;

import com.swatching.swatching_be.domain.brand.BrandVisibility;
import com.swatching.swatching_be.domain.brand.dto.AdminBrandDto;
import com.swatching.swatching_be.domain.brand.service.AdminBrandService;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.domain.user.UserRole;
import com.swatching.swatching_be.global.auth.CurrentUserProvider;
import com.swatching.swatching_be.global.common.ApiResponse;
import com.swatching.swatching_be.global.exception.BusinessException;
import com.swatching.swatching_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {

    private final AdminBrandService adminBrandService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminBrandDto.Response>>> getBrands(
            @RequestParam(required = false) BrandVisibility status) {
        checkAdmin();
        List<AdminBrandDto.Response> data = adminBrandService.getBrands(status);
        return ResponseEntity.ok(ApiResponse.success("브랜드 목록 조회 완료", data));
    }

    @PatchMapping("/{brandId}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long brandId) {
        checkAdmin();
        adminBrandService.approve(brandId);
        return ResponseEntity.ok(ApiResponse.success("승인이 완료되었습니다.", null));
    }

    @PatchMapping("/{brandId}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long brandId,
            @RequestBody AdminBrandDto.RejectRequest request) {
        checkAdmin();
        adminBrandService.reject(brandId, request.getReason());
        return ResponseEntity.ok(ApiResponse.success("반려 처리되었습니다.", null));
    }

    private void checkAdmin() {
        User user = currentUserProvider.getCurrentUser();
        if (user.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "관리자만 접근할 수 있습니다.");
        }
    }
}
