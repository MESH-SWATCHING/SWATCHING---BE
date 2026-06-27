package com.swatching.swatching_be.domain.brand.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatching.swatching_be.domain.brand.dto.BrandDeckResponseDto;
import com.swatching.swatching_be.domain.brand.dto.BrandDetailResponse;
import com.swatching.swatching_be.domain.brand.dto.BrandRecommendResponse;
import com.swatching.swatching_be.domain.brand.dto.BrandResponseDto;
import com.swatching.swatching_be.domain.brand.dto.BrandSubmitRequest;
import com.swatching.swatching_be.domain.brand.service.BrandService;
import com.swatching.swatching_be.domain.image.service.ImageUploadService;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.global.auth.CurrentUserProvider;
import com.swatching.swatching_be.global.common.ApiResponse;
import com.swatching.swatching_be.global.exception.BusinessException;
import com.swatching.swatching_be.global.exception.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final CurrentUserProvider currentUserProvider;
    private final ImageUploadService imageUploadService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandResponseDto>>> getBrands(
            @RequestParam(required = false) String keywords
    ) {
        User user = currentUserProvider.getCurrentUser();
        List<BrandResponseDto> data = brandService.getBrands(keywords, user);
        return ResponseEntity.ok(ApiResponse.success("Brands loaded.", data));
    }

    @GetMapping("/brands/deck")
    public ResponseEntity<ApiResponse<BrandDeckResponseDto>> getBrandDeck(
            @RequestParam(required = false) String keywords
    ) {
        User user = currentUserProvider.getCurrentUser();
        BrandDeckResponseDto data = brandService.getBrandDeck(keywords, user);
        return ResponseEntity.ok(ApiResponse.success("Brand deck loaded.", data));
    }

    @GetMapping("/brands/{brandId}")
    public BrandDetailResponse getBrandDetail(@PathVariable Long brandId) {
        return brandService.getBrandDetail(brandId);
    }

    @PostMapping(value = "/brands/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> submitBrand(@Valid @RequestBody BrandSubmitRequest request) {
        User user = currentUserProvider.getCurrentUser();
        brandService.submitBrand(request, user);
        return ResponseEntity.ok(ApiResponse.success("Brand submission completed.", null));
    }

    @PostMapping(value = "/brands/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> submitBrandWithImages(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "visuals", required = false) List<MultipartFile> visuals
    ) {
        User user = currentUserProvider.getCurrentUser();
        BrandSubmitRequest request = parseSubmitRequest(requestJson);

        String mainImageUrl = null;
        if (mainImage != null && !mainImage.isEmpty()) {
            mainImageUrl = imageUploadService.uploadImage(user, mainImage).imageUrl();
        }

        List<String> visualImageUrls = new ArrayList<>();
        if (visuals != null) {
            visuals.stream()
                    .filter(visual -> visual != null && !visual.isEmpty())
                    .map(visual -> imageUploadService.uploadImage(user, visual).imageUrl())
                    .forEach(visualImageUrls::add);
        }

        brandService.submitBrand(request, user, mainImageUrl, visualImageUrls);
        return ResponseEntity.ok(ApiResponse.success("Brand submission completed.", null));
    }

    @GetMapping("/brands/{brandId}/recommend")
    public List<BrandRecommendResponse> getRecommendedBrands(@PathVariable Long brandId) {
        return brandService.getRecommendedBrands(brandId);
    }

    private BrandSubmitRequest parseSubmitRequest(String requestJson) {
        try {
            BrandSubmitRequest request = objectMapper.readValue(requestJson, BrandSubmitRequest.class);
            validateSubmitRequest(request);
            return request;
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "request must be valid JSON.");
        }
    }

    private void validateSubmitRequest(BrandSubmitRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BusinessException(ErrorCode.INVALID_REQUEST, message);
        }
    }
}
