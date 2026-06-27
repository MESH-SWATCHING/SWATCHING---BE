package com.swatching.swatching_be.domain.manualbrand.controller;

import com.swatching.swatching_be.domain.manualbrand.dto.CreateManualBrandRequest;
import com.swatching.swatching_be.domain.manualbrand.dto.ManualBrandResponse;
import com.swatching.swatching_be.domain.manualbrand.service.ManualBrandService;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.global.auth.CurrentUserProvider;
import com.swatching.swatching_be.global.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manual-brands")
public class ManualBrandController {

    private final CurrentUserProvider currentUserProvider;
    private final ManualBrandService manualBrandService;

    public ManualBrandController(CurrentUserProvider currentUserProvider, ManualBrandService manualBrandService) {
        this.currentUserProvider = currentUserProvider;
        this.manualBrandService = manualBrandService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ManualBrandResponse> createManualBrand(@Valid @RequestBody CreateManualBrandRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        return ApiResponse.success("직접 브랜드 생성 및 저장 성공", manualBrandService.createManualBrand(currentUser, request));
    }
}
