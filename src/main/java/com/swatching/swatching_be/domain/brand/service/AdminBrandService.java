package com.swatching.swatching_be.domain.brand.service;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandVisibility;
import com.swatching.swatching_be.domain.brand.dto.AdminBrandDto;
import com.swatching.swatching_be.domain.brand.repository.BrandRepository;
import com.swatching.swatching_be.global.exception.BusinessException;
import com.swatching.swatching_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBrandService {

    private final BrandRepository brandRepository;

    public List<AdminBrandDto.Response> getBrands(BrandVisibility status) {
        List<Brand> brands = (status == null)
                ? brandRepository.findAllByOrderByCreatedAtDesc()
                : brandRepository.findAllByVisibilityOrderByCreatedAtDesc(status);

        return brands.stream()
                .map(AdminBrandDto.Response::from)
                .toList();
    }

    @Transactional
    public void approve(Long brandId) {
        Brand brand = findBrand(brandId);
        brand.approve();
    }

    @Transactional
    public void reject(Long brandId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "반려 사유를 입력해주세요.");
        }
        Brand brand = findBrand(brandId);
        brand.reject(reason);
    }

    private Brand findBrand(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "브랜드를 찾을 수 없습니다."));
    }
}
