package com.swatching.swatching_be.domain.manualbrand.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatching.swatching_be.domain.image.service.ImageUploadService;
import com.swatching.swatching_be.domain.manualbrand.dto.CreateManualBrandRequest;
import com.swatching.swatching_be.domain.manualbrand.dto.ManualBrandResponse;
import com.swatching.swatching_be.domain.manualbrand.service.ManualBrandService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/manual-brands")
public class ManualBrandController {

    private final CurrentUserProvider currentUserProvider;
    private final ManualBrandService manualBrandService;
    private final ImageUploadService imageUploadService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public ManualBrandController(
            CurrentUserProvider currentUserProvider,
            ManualBrandService manualBrandService,
            ImageUploadService imageUploadService,
            ObjectMapper objectMapper,
            Validator validator
    ) {
        this.currentUserProvider = currentUserProvider;
        this.manualBrandService = manualBrandService;
        this.imageUploadService = imageUploadService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ManualBrandResponse> createManualBrand(@Valid @RequestBody CreateManualBrandRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        return ApiResponse.success("Manual brand created.", manualBrandService.createManualBrand(currentUser, request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ManualBrandResponse> createManualBrandWithImages(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        User currentUser = currentUserProvider.getCurrentUser();
        CreateManualBrandRequest request = parseRequest(requestJson);

        String mainImageUrl = null;
        if (mainImage != null && !mainImage.isEmpty()) {
            mainImageUrl = imageUploadService.uploadImage(currentUser, mainImage).imageUrl();
        }

        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            images.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .map(image -> imageUploadService.uploadImage(currentUser, image).imageUrl())
                    .forEach(imageUrls::add);
        }

        return ApiResponse.success(
                "Manual brand created with images.",
                manualBrandService.createManualBrand(currentUser, request, mainImageUrl, imageUrls)
        );
    }

    private CreateManualBrandRequest parseRequest(String requestJson) {
        try {
            CreateManualBrandRequest request = objectMapper.readValue(requestJson, CreateManualBrandRequest.class);
            validateRequest(request);
            return request;
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "request must be valid JSON.");
        }
    }

    private void validateRequest(CreateManualBrandRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BusinessException(ErrorCode.INVALID_REQUEST, message);
        }
    }
}
